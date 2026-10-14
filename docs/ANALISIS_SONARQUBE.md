# Análisis SonarQube — Gestión Gras Sintético (Backend)

> **Informe**: instalación de plugins de calidad, resultados de los análisis y ajuste de
> métricas según el modelo de *Maintainability* de **Ibarra et al.** —
> *"Map between SonarQube's Maintainability to ISO/IEC 25023"* [1], alineado con
> ISO/IEC 25010:2023 y ISO/IEC 25023:2016 [2][3].
>
> Curso: Calidad de Producto de Software · Unidad II · Fecha: 2026-10-13 · **Fases 0–6 completadas**

---

## 1. Resumen ejecutivo

| Elemento | Resultado |
|---|---|
| SonarQube | Community **26.7.0.124771** en Docker, `http://localhost:9000` |
| Plugins instalados | **SonarPMD 4.2.1** · **Sonar Checkstyle 13.7.0** · **Sonar FindBugs 4.7.0** (los 3 cargados, EXTERNAL) |
| Proyecto | `AnaliZelaya_Gestion_Grass_Sintetico` (*Gestion Grass Sintetico - Backend*) |
| Quality Profile | **"Java PMD Taller"** — **1406 reglas activas** (849 java + 48 checkstyle + 509 findbugs), **por defecto**, asociada al proyecto |
| Análisis final | `BUILD SUCCESS` (verify, 1 test OK) + `ANALYSIS SUCCESSFUL` + CE `SUCCESS` |
| Resultado final | **1647 issues** = 1626 *code smells* + 13 vulnerabilidades + 8 bugs (por repo: pmd 1312, java 188, checkstyle 85, findbugs 62) |
| **Score Maintainability** | **0.675 / 1.0** (ver §7) |
| Dashboard | `http://localhost:9000/dashboard?id=AnaliZelaya_Gestion_Grass_Sintetico` |

---

## 2. Infraestructura

| Componente | Detalle |
|---|---|
| Servidor | Contenedor Docker `sonarqube` (imagen `sonarqube:community`), puerto `9000`, H2 embebida |
| Acceso | `admin` / `admin` — restaurada con el SQL oficial de SonarSource sobre H2 vía `org.h2.tools.Shell` (la UI pedirá cambiar la contraseña en el primer login) |
| Proyecto | Clave: `AnaliZelaya_Gestion_Grass_Sintetico` |
| Token de análisis | `sqp_2d6cedbb83be5ce0…98b70` (token `PROJECT_ANALYSIS`, guárdalo en lugar seguro; se puede regenerar en *My Account → Security*) |
| Código analizado | `grass-potokar-backend` (Java 17, Spring Boot 3.2.4, Maven wrapper) |

---

## 3. Instalación del plugin (8 pasos de la guía)

| # | Paso | Acción ejecutada | Resultado |
|---|---|---|---|
| 1 | Levantar servidor | `docker start sonarqube` → `GET /api/system/status` | `UP` |
| 2 | Acceso administrador | Reset de contraseña con hash oficial SonarSource sobre H2 (`jdbc:h2:tcp://localhost:9092/sonar`) + `api/authentication/validate` | `{"valid":true}` |
| 3 | Descargar plugin | `sonar-pmd-plugin-4.2.1.jar` (27,9 MB) desde `github.com/jborgers/sonar-pmd/releases/tag/4.2.1` → `D:\Temp\` | Descarga OK |
| 4 | Instalar | `docker cp` → `/opt/sonarqube/extensions/plugins/` + `docker restart` | Plugin `pmd` v4.2.1 cargado (`api/plugins/installed`) |
| 5 | Proyecto y token | `POST /api/projects/create` + `POST /api/user_tokens/generate` | Proyecto y token creados |
| 6 | Quality Profile | Copia de *Sonar way* → **"Java PMD Taller"**; activación de reglas PMD con `activate_rules?targetKey=…&repositories=pmd&languages=java` (291/292; `pmd:XPathRule` es plantilla y no aplica); `set_default` + `add_project` | **846 reglas**, default ✔, asociada ✔ |
| 7 | Análisis | `mvnw.cmd clean verify` → `mvnw.cmd org.sonarsource.scanner.maven:sonar-maven-plugin:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=… -Dsonar.projectKey=AnaliZelaya_Gestion_Grass_Sintetico` | `BUILD SUCCESS` + `ANALYSIS SUCCESSFUL` |
| 8 | Verificación | `api/ce/task` → `SUCCESS`; `api/issues/search?facets=rules` | 1483 issues, 1312 PMD |

> **APIs aprendidas** (útiles para repetir): `delete/set_default` usan
> `qualityProfile=<nombre>` + `language`; `activate_rules` filtra con los parámetros
> genéricos de búsqueda (`rule_key`, `repositories`, `languages`, `q`, …) — **no** con `rules`;
> asociación de proyecto: `add_project?project=…&qualityProfile=…&language=…`.

### Detalle del Quality Profile (estado final)

| Campo | Valor |
|---|---|
| Nombre | `Java PMD Taller` |
| Key | `5330c0ce-d05b-46ad-a541-751c323c21e0` (UUID en BD: `dea61a55-4a4d-45b7-bc36-49e6197c4500`) |
| Base | *Sonar way* (Java) = 555 reglas |
| + PMD | +291 reglas del repositorio `pmd` → **846** |
| + Reglas del modelo (Fase 2) | `java:S1541`, `java:S1200`, `java:S138` → **849** |
| + Checkstyle (Fase 4) | 48 reglas concretas (156 *templates* no activables) → **897** |
| + FindBugs (Fase 4) | +509 reglas → **1406 reglas activas** |
| Umbrales del paper (Fase 2) | `S138 max=30` ✔ · `S1541 Threshold=10` ✔ · `S1200 max=20` ✔ · `S3776 Threshold=15` ✔ (verificados por export XML) |
| Default / proyecto | Sí / `AnaliZelaya_Gestion_Grass_Sintetico` |

---

## 4. Análisis inicial — resultados

- **1483 issues** totales (1312 de PMD = **88 %**).
- Top reglas PMD:

| Regla | Issues |
|---|---|
| `pmd:CommentRequired` | 421 |
| `pmd:MethodArgumentCouldBeFinal` | 264 |
| `pmd:LocalVariableCouldBeFinal` | 227 |
| `pmd:LawOfDemeter` | 46 |
| `pmd:AtLeastOneConstructor` | 42 |
| `pmd:ShortVariable` | 32 |
| `pmd:LongVariable` | 31 |
| `pmd:OnlyOneReturn` | 26 |
| `pmd:ControlStatementBraces` | 24 |
| `pmd:DataClass` | 21 |

- El build también ejecuta **Checkstyle, SpotBugs (+findsecbugs) y JaCoCo** (configurados en `pom.xml`); `target/site/jacoco/jacoco.xml` se genera en `verify`.

### Análisis final (tras Fases 2–5)

| Métrica | Inicial | Final |
|---|---|---|
| Issues totales | 1483 | **1647** |
| Code smells | 1464 | **1626** |
| Vulnerabilidades | — | **13** |
| Bugs (findbugs) | — | **8** |
| Deuda técnica (`sqale_debt_ratio`) | 14.1 % | **15.1 %** |
| Cobertura | 4.9 % | 4.9 % |
| Duplicación | 5.5 % | 5.5 % |

Issues por repositorio de reglas: `pmd` 1312 · `java` 188 · `checkstyle` 85 · `findbugs` 62.
Top final: `pmd:CommentRequired` 421, `pmd:MethodArgumentCouldBeFinal` 264,
`pmd:LocalVariableCouldBeFinal` 227, `java:S6813` 52, `checkstyle:MissingCtorCheck` 42,
`findbugs:EI_EXPOSE_REP` 18 + `EI_EXPOSE_REP2` 18.

---

## 5. Diagnóstico vs. [PDF 1] — modelo de Maintainability

El paper propone medir las **5 sub-características de Maintainability (ISO/IEC 25010)**
con métricas de SonarQube (ISO/IEC 25023). Fórmulas del paper (Tablas 2 y 3):

| Sub-característica | Métrica (fórmula) | Regla / métrica SonarQube | Score final (§7) |
|---|---|---|---|
| **Modularidad** | 1) Adecuación de complejidad ciclomática: `X = 1 − A/B` (A = métodos que violan *complejidad > 10*, `java:S1541`; B = total de funciones) | Métrica *Size: Functions* | ✔ 0.992 (4/476) |
| | 2) Acoplamiento de componentes: `X = 1 − A/B` (A = clases que violan *acoplamiento > 20*, `java:S1200`; B = total de clases) | Métrica *Size: Classes* | ✔ 0.972 (2/72) |
| **Reusabilidad** | 1) Reusabilidad de activos: `X = 1 − A/B` (A = métodos > 30 LOC, `java:S138`; A' = clases > 300 LOC; B = funciones/clases) | Tamaño de método/clase | ✔ 0.977 / 1.000 |
| | 2) Conformidad a reglas de codificación: `X = 1 − A/B` (A = clases con ≥1 *code smell*; B = total de clases) | Métrica *code smells* | ✔ 0.014 (71/72) |
| **Analizabilidad** | Complejidad cognitiva: `X = 1 − A/B` (A = métodos que violan *complejidad cognitiva*, `java:S3776`; B = total de clases) | *Cognitive complexity* | ✔ 0.944 (4/72) |
| **Modificabilidad** | 1) Deuda técnica: `X = 1 − r` (r = *technical debt ratio*) · 2) Duplicación: `X = 1 − d` (d = *duplicate line density*) | `sqale_debt_ratio`, `duplicated_lines_density` | ✔ 0.849 / 0.945 |
| **Testabilidad** | Completitud de pruebas: `X = Coverage` | `coverage` (JaCoCo) | ✔ 0.049 (1 test) |

**No mapeables con SonarQube** (según el paper): *System Log Completeness*, *Diagnostic Function
Effectiveness/Sufficiency*, *Modification Correctness/Capacity/Efficiency*, *Autonomous
Testability*, *Test Restartability* (requieren historial de versiones/documentación).

> Nota: el paper se validó sobre **SonarQube 10.7**; aquí corremos **26.7**, por lo que en la
> Fase 1 se verificaron las claves reales de métricas/reglas vigentes (§6).

---

## 6. Ejecución del plan (Fases 0–6)

Objetivo cumplido: el servidor produce **todas las medidas** para las 5 sub-características
y corre con **3 plugins** de análisis.

### Fase 0 — Prerequisitos ✔
- Docker Desktop iniciado; `docker start sonarqube` → `api/system/status = UP`.
- **Nota**: al reiniciar Docker también hubo que levantar el contenedor `pg-grass`
  (PostgreSQL 16, puerto 5432); sin él, los tests de `verify` fallan con
  `Connection to localhost:5432 refused`.

### Fase 1 — Diagnóstico por API (solo lectura) ✔
- `api/metrics/search` → **155 métricas**; todas las del paper disponibles
  (`classes`, `functions`, `complexity`, `cognitive_complexity`, `coverage`,
  `sqale_debt_ratio`, `sqale_index`, `duplicated_lines_density`, `code_smells`, `ncloc`…).
- `pmd:ExcessiveClassLength` **no existe** en sonar-pmd 4.2.1 ⇒ tamaño de clase (>300 LOC)
  se calcula con `ncloc` por archivo (`api/measures/component_tree`) — ver §7.

### Fase 2 — Quality Profile: reglas y umbrales ✔
- Activadas `java:S1541`, `java:S1200`, `java:S138` (`java:S3776` ya estaba): 846 → **849**.
- Umbrales fijados con `POST /api/qualityprofiles/activate_rule?…&rule=java:S138&params=max%3D30`;
  verificados con `api/qualityprofiles/export` (XML): **S138 max=30, S1541 Threshold=10,
  S1200 max=20, S3776 Threshold=15**.

| Parámetro | Valor paper | Valor aplicado |
|---|---|---|
| Complejidad de método (`java:S1541`) | **10** | 10 ✔ |
| Acoplamiento de clase (`java:S1200`) | **20** | 20 ✔ |
| Líneas de método (`java:S138`) | **30** | 30 ✔ |
| Líneas de clase | **300** | vía `ncloc` (>300 LOC), sin regla equivalente ✔ |
| Complejidad cognitiva (`java:S3776`) | defecto (15) | 15 ✔ |

### Fase 3 — Cobertura (Testabilidad) ✔
- JaCoCo importa `target/site/jacoco/jacoco.xml` → `coverage = 4.9 %` (1 clase de test,
  1 test). Subirla requiere añadir tests (trabajo aparte del taller).

### Fase 4 — Plugins adicionales ✔
1. `api/plugins/install` (marketplace) devolvió 204 pero se quedó en *installing* ⇒
   descarga manual desde GitHub + `docker cp` + `docker restart`:
   | Plugin | Versión | Origen |
   |---|---|---|
   | SonarPMD | 4.2.1 | `github.com/jborgers/sonar-pmd/releases` |
   | Sonar Checkstyle | 13.7.0 | `github.com/checkstyle/sonar-checkstyle/releases` |
   | Sonar FindBugs | 4.7.0 | `github.com/spotbugs/sonar-findbugs/releases` |
2. Activación por repositorio (`activate_rules?…&repositories=checkstyle|findbugs`):
   - `findbugs` → **509/509** activadas.
   - `checkstyle` → **48** activadas; 156 son *templates* (`…Checktemplate`) que requieren
     instanciarse con parámetros y no se activan en bloque.
3. El sensor FindBugs **reutiliza** el reporte `target/spotbugsXml.xml` que ya generaba
   el build (plugin `spotbugs-maven-plugin`); Checkstyle genera su reporte en el escaneo.

### Fase 5 — Re-análisis ✔
- `pom.xml`: `<sonar.host.url>` cambiado de `https://sonarcloud.io` a **`http://localhost:9000`**.
- `mvnw.cmd clean verify` → `BUILD SUCCESS` (1 test, 0 fallos) +
  `mvnw.cmd org.sonarsource.scanner.maven:sonar-maven-plugin:sonar` →
  **`ANALYSIS SUCCESSFUL`**, sensores `CheckstyleSensor` y `FindBugs Sensor` ejecutados,
  CE task `SUCCESS`.

### Fase 6 — Cálculo de scores ✔
- Script Python (`calc_scores.py`) que consulta la API REST (medidas + issues paginados +
  `component_tree`) y aplica las fórmulas del paper → resultados en §7.

---

## 7. Resultados del modelo (post-ajuste)

Cálculo con la API REST tras el análisis final (script `calc_scores.py`, fórmulas de las
Tablas 2 y 3 del paper; redondeo a 3 decimales).

| Sub-característica | Fórmula | A | B | Score |
|---|---|---|---|---|
| Modularidad — complejidad ciclomática (`java:S1541`, umbral 10) | `1 − A/B` | 4 | 476 funciones | **0.992** |
| Modularidad — acoplamiento (`java:S1200`, umbral 20) | `1 − A/B` | 2 | 72 clases | **0.972** |
| **Modularidad** (promedio) | — | — | — | **0.982** |
| Reusabilidad — método >30 LOC (`java:S138`) | `1 − A/B` | 11 | 476 funciones | **0.977** |
| Reusabilidad — clase >300 LOC (`ncloc`) | `1 − A/B` | 0 | 72 clases | **1.000** |
| Reusabilidad — conformidad: clases con ≥1 code smell | `1 − A/B` | **71** | 72 clases | **0.014** |
| **Reusabilidad** (promedio de los 3) | — | — | — | **0.501** |
| Analizabilidad — complejidad cognitiva (`java:S3776`) | `1 − A/B` | 4 | 72 clases | **0.944** |
| Modificabilidad — deuda técnica | `1 − r`, r = 0.151 | — | — | **0.849** |
| Modificabilidad — duplicación | `1 − d`, d = 0.055 | — | — | **0.945** |
| **Modificabilidad** (promedio) | — | — | — | **0.897** |
| Testabilidad — cobertura (JaCoCo) | `Coverage` | — | — | **0.049** |
| **Mantenibilidad global** (promedio de las 5) | — | — | — | **0.675** |

**Interpretación**

- **Modularidad (0.982)** y **Analizabilidad (0.944)** son las mejores: solo 4 métodos
  superan 10 de complejidad ciclomática/cognitiva y 2 clases exceden 20 de acoplamiento.
- **Reusabilidad (0.501)** se hunde por la métrica de *conformidad a reglas de
  codificación*: **71 de 72 clases (99 %)** tienen al menos un code smell — arrastrado por
  ruidos como `pmd:CommentRequired` (421) y `*CouldBeFinal` (491). Ninguna clase supera
  300 LOC (score 1.000). Es el score más sensible a la elección de reglas activas.
- **Modificabilidad (0.897)**: deuda técnica 15.1 % y duplicación 5.5 %.
- **Testabilidad (0.049)** es el problema principal: solo existe
  `GrassBackendApplicationTests` (1 test); añadir pruebas es la palanca de mejora más
  fuerte para el score global (cada +10 % de cobertura sube global ≈ +0.020).

> Supuestos: (1) *A/B* usa `1 − A/B` del paper sin recorte; (2) Reusabilidad =
> promedio de los 3 indicadores; (3) global = promedio simple de las 5
> sub-características; (4) A y B restringidos a `src/main/java`; (5) *B funciones/clases*
> = métricas `functions`/`classes` de SonarQube.

---

## 8. Referencias

1. Ibarra, R., Castillo, J., Pando, B., Dávila, A. *Map between SonarQube's Maintainability to ISO/IEC 25023* (Cameraready 980495, UNAS/PUCP).
2. ISO/IEC 25010:2023 — *Systems and software Quality Requirements and Evaluation (SQuaRE) — Product quality model*.
3. ISO/IEC 25023:2016 — *SQuaRE — Measurement of System and Software Product Quality*.
4. Guía de instalación del plugin (Unidad II): `UNIDAD II/Guia_Instalacion_Plugin_SonarQube.md`.
5. SonarPMD 4.2.1 — <https://github.com/jborgers/sonar-pmd/releases/tag/4.2.1>.
6. SonarQube Web API — <https://docs.sonarsource.com/sonarqube/latest/extension-guide/web-api/>.
7. Sonar Checkstyle 13.7.0 — <https://github.com/checkstyle/sonar-checkstyle/releases>.
8. Sonar FindBugs 4.7.0 — <https://github.com/spotbugs/sonar-findbugs/releases>.
9. Script de cálculo de scores: `docs/calc_scores.py` (requiere Python 3, sin dependencias externas).
