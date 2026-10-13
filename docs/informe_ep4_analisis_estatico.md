# INFORME DE ANÁLISIS ESTÁTICO (EP4)

## Fase 4.1: Evaluación de calidad interna — Backend Grass Sintetico

| Campo | Valor |
|---|---|
| Producto software | `grass-backend` (Spring Boot 3.2.4, Java 17) |
| Repositorio | https://github.com/AnaliZelaya/Gestion_Grass_Sintetico |
| Repositorio origen (fork) | https://github.com/JosephInocente/grass-potokar-backend |
| Fecha del análisis | 12/10/2026 |
| Analista estático | Anali Zelaya (rol: Analista estático) |
| Norma de referencia | ISO/IEC 25040 — Fase 4.1; ISO/IEC 25010:2023 (mantenibilidad) |

---

## 1. METODOLOGÍA

El análisis estático se ejecutó **sin ejecutar el programa** sobre la totalidad del
código fuente de producción (`src/main/java`), aplicando cuatro herramientas
complementarias para cubrir las siete métricas exigidas por el entregable:

| # | Métrica exigida (EP4 §1.3) | Herramienta principal | Complemento |
|---|---|---|---|
| 1 | Complejidad ciclomática | SonarQube (`complexity`) | Checkstyle (por método), PMD |
| 2 | Acoplamiento y cohesión | SonarQube / PMD (`LawOfDemeter`, `CouplingBetweenObjects`) | Checkstyle (`ClassFanOutComplexity`) |
| 3 | Índice de mantenibilidad | SonarQube (`sqale_rating`, `sqale_debt_ratio`) | — |
| 4 | Deuda técnica | SonarQube (`sqale_index`) | — |
| 5 | Code smells | SonarQube (`code_smells`) | PMD (168 violaciones) |
| 6 | Vulnerabilidades | SonarQube (`vulnerabilities`) | SpotBugs + FindSecBugs (123 bugs) |
| 7 | Duplicación de código | SonarQube (`duplicated_lines_density`) | PMD CPD (4 bloques) |

**Justificación del complemento (§4 del enunciado):** SonarQube *no expone a detalle*
la complejidad por método, la profundidad de acoplamiento ni la detección de
inyección de dependencias por campo; por eso se complementó con **Checkstyle**
(complejidad ciclomática/NPath/Fan-Out por clase), **PMD** (168 reglas de
diseño/mantenibilidad) y **SpotBugs + FindSecBugs** (análisis de bytecode con
reglas de seguridad Spring). **JaCoCo** aporta la cobertura que SonarQube solo
resume.

### 1.1. Entorno de análisis (banco de pruebas — Fase 3.3.1)

| Componente | Versión / detalle |
|---|---|
| SO | Microsoft Windows 11 Home Single Language 10.0.26200 |
| CPU / RAM | Intel Core i7-13620H (16 hilos) / 15.7 GB |
| JDK | Oracle JDK 24.0.2 (compila con `--release 17`) |
| Maven | 3.9.14 |
| SonarQube | **SonarCloud** (nube), organización `analizelaya`, proyecto `AnaliZelaya_Gestion_Grass_Sintetico` |
| Quality Profile | `Sonar way core` (perfil por defecto, **308 reglas Java activas**) |
| Quality Gate | `Sonar way` (asociado vía API el 12/10/2026) |
| Scanner | `sonar-maven-plugin` 3.9.1.2184 |
| PMD | maven-pmd-plugin 3.28.0 / PMD 7.17.0 (bestpractices, design, multithreading, errorprone) |
| Checkstyle | maven-checkstyle-plugin 3.6.0 / Checkstyle 9.3, config propia `config/checkstyle/checkstyle.xml` (umbrales: ciclomática ≤15, NPath ≤250, Fan-Out ≤25, método ≤150 líneas) |
| SpotBugs | maven-plugin 4.10.5.0, `effort=Max`, `threshold=Low` + **FindSecBugs 1.13.0** |
| JaCoCo | 0.8.13 |
| Base de datos (tests) | PostgreSQL 16 (Docker), requerida por el único test de contexto |

**Comandos reproducibles:**

```bash
mvn -B verify                          # compile + test + JaCoCo + PMD + Checkstyle + SpotBugs
mvn -B sonar:sonar -Dsonar.token=<TOKEN>   # análisis SonarCloud
```

### 1.2. Alcance del código analizado (lo que se registra)

SonarQube registra **72 archivos** (71 Java + 1 fichero de recursos):

| Elemento | Valor |
|---|---|
| Archivos (`files`) | 72 |
| Líneas de código (`ncloc`) | **3 440** |
| Sentencias (`statements`) | 1 294 |
| Funciones/métodos (`functions`) | 476 |
| Paquetes | `config`, `controller` (14), `dto` (8), `entity` (13), `exception` (2), `mapper` (3), `repository` (11), `scheduler`, `security` (3), `service` (17) |
| Test | 1 archivo (`GrassBackendApplicationTests`), solo smoke test de contexto |
| Exclusiones configuradas | `**/dto/**` y `**/entity/**` del CPD (datos simples), `**/config/**` de cobertura |

---

## 2. MÉTRICAS OBTENIDAS (2.1–2.7)

### 2.1. Métricas globales SonarCloud

| Métrica | Clave | Valor | Umbral de aceptación | ¿Cumple? |
|---|---|---|---|---|
| Líneas de código | `ncloc` | 3 440 | — (informativo) | — |
| Complejidad total | `complexity` | **676** | ≤ 5 × LOC/100 (342) ₠ | **NO** |
| Complejidad cognitiva | `cognitive_complexity` | **325** | ≤ 150 | **NO** |
| Code smells | `code_smells` | **52** | 0 (calidad A) | **NO** |
| Bugs | `bugs` | **4** | 0 | **NO** |
| Vulnerabilidades | `vulnerabilities` | **12** | 0 | **NO** |
| Security hotspots | `security_hotspots` | 0 | 0 | SÍ |
| Duplicación | `duplicated_lines_density` | **5,5 %** | < 3 % | **NO** |
| Cobertura | `coverage` | **4,9 %** | ≥ 80 % | **NO** |
| Deuda técnica (maintainability) | `sqale_index` | **260 min** (≈ 4,3 h) | ≤ 120 min | **NO** |
| Índice de deuda | `sqale_debt_ratio` | 0,3 % | ≤ 5 % | SÍ |
| **Rating Mantenibilidad** | `sqale_rating` | **A (1,0)** | A | **SÍ** |
| **Rating Fiabilidad** | `reliability_rating` | **D (4,0)** | A | **NO** |
| **Rating Seguridad** | `security_rating` | **E (5,0)** | A | **NO** |
| Quality Gate | `alert_status` | **OK** * | — | SÍ* |

\* El Quality Gate `Sonar way` solo evalúa **código nuevo** (period de “previous version”);
como todo el código es nuevo, condiciona únicamente ratings del periodo. Las
métricas **globales** (arriba) incumplen los umbrales de aceptación definidos.

### 2.2. Complejidad ciclomática (Checkstyle, por método)

Umbrales: warn ≤ 15, error > 15. **3 métodos exceden el umbral:**

| Archivo | Método / línea | Complejidad ciclomática | NPath |
|---|---|---|---|
| `DashboardService.java` | línea 28 | **34** (máx 15) | 7 056 |
| `DataSeederController.java` | línea 234 | **32** (máx 15) | 192 027 |
| `DataSeederController.java` | línea 56 (`generarSoloReservas`, 172 líneas) | **20** (máx 15) | 4 686 |
| `ReservaServiceImpl.java` | línea 277 | 14 (OK) | 264 (supera 250) |

PMD confirma 8 violaciones `CyclomaticComplexity` + 4 `CognitiveComplexity`.

### 2.3. Acoplamiento y cohesión

| Indicador | Hallazgo |
|---|---|
| `ClassFanOutComplexity` | `DataSeederController` = **30** dependencias directas (máx 25) |
| `CouplingBetweenObjects` | `DataSeederController` viola el umbral PMD |
| `LawOfDemeter` (PMD) | **46 violaciones** (mayor regulador de code smells) |
| `LooseCoupling` (PMD) | 3 (dependencia de tipos concretos en vez de interfaces) |
| `DataClass` (PMD) | 21 clases solo con getters/setters → cohesión baja en `dto`/`entity` |
| Inyección por campo | **52** instancias `@Autowired` de campo (ver hallazgo H-04) |

### 2.4. Índice de mantenibilidad y deuda técnica

| Métrica | Valor | Interpretación |
|---|---|---|
| `sqale_rating` | **A** | La deuda relativa (0,3 %) es aceptable: el código no está “en deuda” por volumen… |
| `sqale_index` | **260 min** | …pero en términos absolutos se estiman 4 h 20 min de refactorización |
| Top deuda por archivo | `DataSeederController` 45 min, `ReservaServiceImpl` 30 min, `VentaServiceImpl` 30 min, `TurnoCajaServiceImpl` 25 min, `UsuarioController` 20 min | |

### 2.5. Code smells

| Herramienta | Conteo | Principales reguladores |
|---|---|---|
| SonarQube | **52** | `java:S6813` × 52 (inyección por campo) |
| PMD | **168** (14 prioridad 1, 5 prioridad 2, 149 prioridad 3) | LawOfDemeter 46, DataClass 21, AvoidThrowingRawExceptionTypes 14, AvoidLiteralsInIfCondition 12, LiteralsFirstInComparisons 11, AvoidCatchingGenericException 9, CyclomaticComplexity 8, CognitiveComplexity 4, SystemPrintln 4 |
| Checkstyle | **101** (3 error, 98 warning) | pestañas 20, líneas >140 9, NPath 4, ciclomática 3, método largo 1 |
| **Total registrado** | **421 hallazgos de mantenibilidad** | |

### 2.6. Vulnerabilidades y bugs (seguridad)

| Herramienta | Conteo | Detalle |
|---|---|---|
| SonarQube `vulnerabilities` | **12** | 1 BLOCKER, 10 CRITICAL, 1 MINOR |
| SonarQube `bugs` | **4** | 2 CRITICAL, 2 MAJOR |
| SpotBugs + FindSecBugs | **123** (61 SECURITY, 36 MALICIOUS_CODE, 14 BAD_PRACTICE, 4 I18N, 6 STYLE, 2 PERFORMANCE) | 1 prioridad 1 (CRITICAL), 39 prioridad 2, 83 prioridad 3 |

Principales: `SPRING_CSRF_PROTECTION_DISABLED` ×1 (cwe-352), `EI_EXPOSE_REP/2` ×36,
`IMPROPER_UNICODE` ×11, `PREDICTABLE_RANDOM` ×2, `SPRING_ENDPOINT` ×46 (informativo).

### 2.7. Duplicación de código

| Herramienta | Valor |
|---|---|
| SonarQube | **5,5 %** (umbral < 3 %) — **NO CUMPLE** |
| PMD CPD | 4 bloques duplicados (≥16 líneas) |

| Bloque | Líneas | Archivos |
|---|---|---|
| 1 | 30 | `AuthController.java` ↔ `AuthController.java` |
| 2 | 24 | `ReservaResponseDTO.java` ↔ `Reserva.java` |
| 3 | 22 | `TurnoCajaResponseDTO.java` ↔ `TurnoCaja.java` |
| 4 | 16 | `ReservaServiceImpl.java` ↔ `ReservaServiceImpl.java` |

---

## 3. HALLAZGOS (12 ≥ mínimo 10)

| ID | Hallazgo | Evidencia | Severidad | Impacto (ISO 25010) |
|---|---|---|---|---|
| **H-01** | Secretos hardcodeados: credenciales de BD en `application.properties` y constante `TOKEN` detectada en `ConsultaController.java:12` | SonarCloud `java:S6418` (BLOCKER); `src/main/resources/application.properties:13-14` | **Crítica** | Seguridad (confidencialidad) |
| **H-02** | Protección CSRF de Spring Security deshabilitada | SonarCloud `java:S4502:36` `SecurityConfig.java`; SpotBugs `SPRING_CSRF_PROTECTION_DISABLED` (cwe-352) | **Crítica** | Seguridad (integridad) |
| **H-03** | Entidades JPA expuestas directamente en endpoints REST (fuga de esquema y sobreexposición de datos) | SonarCloud `java:S4684` × 9 (`CanchaController:45,60`, `ReservaController:33`, `RolController:23`, `SedeController:25,31`, `UsuarioController:39,111`, `VentaController:27`) | **Crítica** | Seguridad, mantenibilidad |
| **H-04** | 52 inyecciones de dependencia por campo (`@Autowired` de campo) en lugar de constructor | SonarCloud `java:S6813` × 52 (p.ej. `AuthController:22,25,28`, `SecurityConfig:24`) | Mayor | Mantenibilidad: testabilidad y refactoring difícil |
| **H-05** | Complejidad ciclomática muy por encima del umbral en 3 métodos | Checkstyle: `DashboardService:28` = **34**, `DataSeederController:234` = **32**, `DataSeederController:56` = **20** (máx 15); NPath de 192 027 | Mayor | Mantenibilidad: analizabilidad, modificabilidad |
| **H-06** | Métodos “God method”: `generarSoloReservas` con **172 líneas** (máx 150) y `DataSeederController` con Fan-Out 30 (máx 25) | Checkstyle `MethodLengthCheck`, `ClassFanOutComplexityCheck` | Mayor | Mantenibilidad: modularidad |
| **H-07** | Acoplamiento excesivo: 46 × LawOfDemeter, 3 × LooseCoupling, 21 × DataClass | PMD (design.xml) | Media | Mantenibilidad: acoplamiento/cohesión |
| **H-08** | Cobertura de pruebas **4,9 %** (91 líneas de 1 460 cubiertas; 1 test de contexto, 0 tests unitarios reales) | JaCoCo CSV: `LINE_COVERED=91`, `LINE_MISSED=1369`; SonarCloud `coverage=4.9` | **Crítica** | Mantenibilidad: testabilidad; confiabilidad no verificada |
| **H-09** | Bugs temporales: `new Random()` creado por llamada y duraciones calculadas sin zona horaria | SonarCloud `java:S2119` × 2 (`DataSeederController:67,273`), `java:S8700` × 2 (`DataSeederController:331`, `ReservaServiceImpl:112`) | Mayor | Confiabilidad: corrección en DST/horarios de reservas |
| **H-10** | Duplicación de código 5,5 % (> 3 %): DTO duplicados con entidades y métodos repetidos | SonarCloud `duplicated_lines_density=5.5`; PMD CPD: `ReservaResponseDTO↔Reserva` (24 L), `TurnoCajaResponseDTO↔TurnoCaja` (22 L), `AuthController` (30 L) | Mayor | Mantenibilidad: modificabilidad |
| **H-11** | Excepciones genéricas capturadas y re-lanzadas crudas (`RuntimeException`), `printStackTrace` y `System.out.println` en producción | PMD: `AvoidCatchingGenericException` ×9, `AvoidThrowingRawExceptionTypes` ×14, `SystemPrintln` ×4, `AvoidPrintStackTrace` ×1 | Media | Mantenibilidad: manejo de errores, confiabilidad |
| **H-12** | Estándar de codificación no cumplido: 20 archivos con pestañas, 9 líneas >140 caracteres, entidades usan `Date` en vez de `java.time` | Checkstyle `FileTabCharacter`, `LineLength`; PMD `ReplaceJavaUtilDate` ×2 | Menor | Mantenibilidad: legibilidad/analizabilidad |

---

## 4. ANÁLISIS DE RESULTADOS — Comparación con umbrales

| Dimensión | Umbral (curso/estándar) | Resultado | Juicio |
|---|---|---|---|
| Mantenibilidad (rating Sonar) | A | **A** (deuda 0,3 %) | ✅ Cumple |
| Deuda técnica absoluta | ≤ 120 min | 260 min | ❌ No cumple |
| Complejidad ciclomática | ≤ 15 / método | 34, 32, 20 | ❌ No cumple |
| Duplicación | < 3 % | 5,5 % | ❌ No cumple |
| Cobertura | ≥ 80 % | 4,9 % | ❌ No cumple |
| Vulnerabilidades | 0 CRITICAL/BLOCKER | 11 (1 BLOCKER + 10 CRITICAL) | ❌ No cumple |
| Bugs | 0 | 4 | ❌ No cumple |
| Code smells | decreciente | 52 (Sonar) + 168 (PMD) | ❌ No cumple |
| Quality Gate (código nuevo) | OK | **OK** | ✅ Cumple (limitado) |

**Interpretación crítica:** el *rating* A de mantenibilidad es engañoso: SonarCloud
lo calcula como **proporción** de deuda frente al volumen, y al ser un proyecto
pequeño (3 440 LOC) la proporción queda baja. Las métricas **absolutas** (deuda
260 min, complejidad 34, cobertura 4,9 %) y los **11 issues de seguridad
CRITICAL/BLOCKER** indican calidad interna **aceptable con observaciones
críticas**, no aceptable sin correcciones.

**Cobertura de herramientas (complementación):** de las 7 métricas exigidas,
SonarQube por sí solo detalla 4 con suficiente granularidad; PMD aporta
acoplamiento/cohesión (LawOfDemeter, DataClass), Checkstyle aporta complejidad
**por método** (que Sonar solo muestra por proyecto/archivo) y SpotBugs/FindSecBugs
aporta 123 hallazgos de bytecode (seguridad Spring, exposición de estado) que
SonarQube no registra como `vulnerabilities`. **Ninguna herramienta cubre las 7
métricas sola: la combinación es necesaria.**

---

## 5. PLAN DE MEJORA (avance Fase 5.4 — priorizado)

| Prioridad | Acción | Hallazgos | Esfuerzo est. |
|---|---|---|---|
| P1 | Mover secretos a variables de entorno (`${DB_PASSWORD}`), rotar credenciales | H-01 | 1 h |
| P1 | Revisar `SecurityConfig`: habilitar CSRF o justificar con análisis de riesgo | H-02 | 2 h |
| P1 | Reemplazar entidades JPA por DTOs en los 9 endpoints `S4684` | H-03 | 4 h |
| P2 | Escribir tests unitarios (JUnit 5 + Mockito) para services; meta cobertura ≥ 70 % | H-08 | 16 h |
| P2 | Refactorizar `DashboardService:28` y `DataSeederController` (extraer lógica, splitter) | H-05, H-06 | 8 h |
| P3 | Convertir `@Autowired` de campo a inyección por constructor (Lombok `@RequiredArgsConstructor`) | H-04 | 3 h |
| P3 | Eliminar duplicación DTO↔Entity (MapStruct ya está en el proyecto) | H-10 | 4 h |
| P3 | Reemplazar `Date` por `LocalDateTime`/`ZonedDateTime`, corregir `S8700` | H-09 | 3 h |
| P4 | Aplicar `System.out`→`Logger`, excepciones tipadas, limpiar pestañas | H-11, H-12 | 3 h |

---

## 6. CONCLUSIONES DE CALIDAD INTERNA

1. El código **compila y pasa su smoke test**, pero fue **no analizable tal cual**
   (dependencias Lombok sin versión → build roto; hallazgo corregido en este fork).
2. **Mantenibilidad:** rating A engañoso; 421 hallazgos de estilo/diseño, 3 métodos
   con complejidad > 2× el umbral y 260 min de deuda real.
3. **Seguridad:** 11 issues CRITICAL/BLOCKER (CSRF deshabilitado, secretos
   hardcodeados, entidades expuestas) → **el producto NO cumple los criterios de
   aceptación de calidad interna** en seguridad.
4. **Testabilidad (ISO 25010):** 4,9 % de cobertura invalida cualquier afirmación
   de regresión; es el riesgo mayor para las unidades siguientes.
5. **Complementación de herramientas:** PMD + Checkstyle + SpotBugs + JaCoCo son
   **necesarios** para cubrir las 7 métricas del EP4; se integran en `pom.xml` y en
   el pipeline `.github/workflows/sonar.yml` (análisis automatizado en CI/CD).

---

## ANEXO A — Evidencias de análisis estático

Archivos en `docs/evidencias/`:

| Archivo | Origen |
|---|---|
| `sonarcloud-metricas.json` | API `measures/component` (12/10/2026) |
| `sonarcloud-qualitygate.json` | API `qualitygates/project_status` (Sonar way → OK) |
| `pmd.xml` | maven-pmd-plugin 3.28.0 — 168 violaciones |
| `cpd-duplicacion.xml` | PMD CPD — 4 bloques duplicados |
| `checkstyle.xml` | Checkstyle 9.3 — 101 hallazgos |
| `spotbugs.xml` | SpotBugs 4.10.5 + FindSecBugs — 123 bugs |
| `jacoco-cobertura.csv` | JaCoCo 0.8.13 — 91/1 460 líneas cubiertas |

Dashboard público SonarCloud:
https://sonarcloud.io/dashboard?id=AnaliZelaya_Gestion_Grass_Sintetico

**Reproducción local:**

```bash
mvn -B verify
mvn -B sonar:sonar "-Dsonar.token=<TOKEN>"
```
