# -*- coding: utf-8 -*-
"""Calcula las 5 sub-caracteristicas de Maintainability (ISO/IEC 25010)
segun el modelo de Ibarra et al. 'Map between SonarQube's Maintainability
to ISO/IEC 25023', usando la API REST de SonarQube."""
import base64
import json
import sys
import urllib.request
import urllib.parse

BASE = "http://localhost:9000"
PROJECT = "AnaliZelaya_Gestion_Grass_Sintetico"
AUTH = "Basic " + base64.b64encode(b"admin:admin").decode()

def get(path, **params):
    if params:
        path = path + "?" + urllib.parse.urlencode(params, safe=",")
    req = urllib.request.Request(BASE + path, headers={"Authorization": AUTH})
    with urllib.request.urlopen(req, timeout=60) as r:
        return json.loads(r.read().decode())

# 1) Medidas del proyecto
metric_keys = ("ncloc,functions,classes,coverage,line_coverage,branch_coverage,"
               "sqale_debt_ratio,duplicated_lines_density,code_smells,sqale_index,"
               "complexity,cognitive_complexity,comment_lines_density,statements")
m = get("/api/measures/component", component=PROJECT, metricKeys=metric_keys)
measures = {x["metric"]: float(x["value"]) for x in m["component"]["measures"] if "value" in x}

# LOC por archivo via component_tree
files_loc = []
p = 1
while True:
    ct = get("/api/measures/component_tree", component=PROJECT, metricKeys="ncloc",
             ps=500, p=p)
    comps = ct.get("components", [])
    for c in comps:
        if not c.get("path") or c.get("qualifier") == "DIR":
            continue
        met = c.get("measures") or []
        val = next((float(x["value"]) for x in met if x["metric"] == "ncloc" and "value" in x), None)
        if val is not None:
            files_loc.append({"path": c["path"], "lines": val})
    if p * 500 >= ct.get("paging", {}).get("total", 0) or not comps:
        break
    p += 1

# 2) Todos los issues (paginado)
issues = []
page = 1
while True:
    r = get("/api/issues/search", componentKeys=PROJECT, ps=500, p=page,
            additionalFields="_all")
    issues.extend(r["issues"])
    if len(issues) >= r["total"] or not r["issues"]:
        break
    page += 1

def in_main(component_key):
    return ":src/main/java/" in component_key

def n_issues(rule_key):
    return sum(1 for i in issues
               if i["rule"] == rule_key and in_main(i["component"]))

MAIN = "src/main/java"
# 3) Valores A y B del modelo
A_complex   = n_issues("java:S1541")                      # metodos complejos >10
A_coupling  = n_issues("java:S1200")                      # clases acopladas >20
A_methodloc = n_issues("java:S138")                       # metodos >30 LOC
A_cognitive = n_issues("java:S3776")                      # complejidad cognitiva
A_classloc  = sum(1 for f in files_loc
                  if f["path"].startswith(MAIN) and f["lines"] > 300)   # clases >300 LOC
smell_files = {i["component"] for i in issues
               if i["type"] == "CODE_SMELL" and in_main(i["component"])}
A_smells = len(smell_files)                                # clases con >=1 code smell
B_functions = measures["functions"]
B_classes   = measures["classes"]

r_debt = measures["sqale_debt_ratio"] / 100.0              # technical debt ratio
d_dup   = measures["duplicated_lines_density"] / 100.0     # duplicate line density
coverage = measures["coverage"] / 100.0

def x1_ab(a, b):
    return 1 - (a / b if b else 0)

scores = {}
scores["Modularidad - complejidad ciclomica"] = (x1_ab(A_complex, B_functions), A_complex, B_functions, "1-A/B")
scores["Modularidad - acoplamiento"]          = (x1_ab(A_coupling, B_classes), A_coupling, B_classes, "1-A/B")
mod_avg = (scores["Modularidad - complejidad ciclomica"][0] + scores["Modularidad - acoplamiento"][0]) / 2

r_size_method = x1_ab(A_methodloc, B_functions)
r_size_class  = x1_ab(A_classloc, B_classes)
r_assets = (r_size_method + r_size_class) / 2
r_rules = x1_ab(A_smells, B_classes)
scores["Reusabilidad - tamano (metodo)"]  = (r_size_method, A_methodloc, B_functions, "1-A/B")
scores["Reusabilidad - tamano (clase)"]   = (r_size_class, A_classloc, B_classes, "1-A/B")
scores["Reusabilidad - conformidad reglas"]= (r_rules, A_smells, B_classes, "1-A/B")
reus_avg = (r_assets + r_rules) / 2

scores["Analizabilidad - complejidad cognitiva"] = (x1_ab(A_cognitive, B_classes), A_cognitive, B_classes, "1-A/B")
an_counts = x1_ab(A_cognitive, B_classes)

scores["Modificabilidad - deuda tecnica"] = (1 - r_debt, None, r_debt, "1-r")
scores["Modificabilidad - duplicacion"]   = (1 - d_dup, None, d_dup, "1-d")
modif_avg = (scores["Modificabilidad - deuda tecnica"][0] + scores["Modificabilidad - duplicacion"][0]) / 2

scores["Testabilidad - cobertura"] = (coverage, None, None, "= coverage")

sub = {
    "Modularidad": mod_avg,
    "Reusabilidad": reus_avg,
    "Analizabilidad": an_counts,
    "Modificabilidad": modif_avg,
    "Testabilidad": coverage,
}
overall = sum(sub.values()) / len(sub)

# 4) Desglose de issues por repositorio/tipo
by_repo = {}
by_type = {}
for i in issues:
    repo = i["rule"].split(":")[0]
    by_repo[repo] = by_repo.get(repo, 0) + 1
    by_type[i["type"]] = by_type.get(i["type"], 0) + 1

by_rule = {}
for i in issues:
    by_rule[i["rule"]] = by_rule.get(i["rule"], 0) + 1

out = {
    "measures": measures,
    "counts": {
        "A_complex(S1541)": A_complex, "A_coupling(S1200)": A_coupling,
        "A_methodloc(S138)": A_methodloc, "A_cognitive(S3776)": A_cognitive,
        "A_classloc(>300)": A_classloc, "A_smells(classes_con_smell)": A_smells,
        "B_functions": B_functions, "B_classes": B_classes,
        "r": r_debt, "d": d_dup, "coverage": coverage,
        "total_issues": len(issues),
    },
    "scores_detalle": {k: {"score": v[0], "A": v[1], "B": v[2], "formula": v[3]}
                       for k, v in scores.items()},
    "subcaracteristicas": sub,
    "mantenibilidad_global": overall,
    "issues_por_repo": dict(sorted(by_repo.items(), key=lambda x: -x[1])),
    "issues_por_tipo": by_type,
    "top_reglas": dict(sorted(by_rule.items(), key=lambda x: -x[1])[:20]),
}
print(json.dumps(out, indent=2, ensure_ascii=False))
