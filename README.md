# Gestión Grass Sintético — Backend

Backend del sistema de gestión de canchas de **grass sintético**: catálogo de canchas y sedes, reservas de clientes, ventas en punto de venta (POS), control de caja y reportes. Desarrollado como proyecto del curso **Calidad de Producto de Software** (Octavo ciclo), donde se evalúa la calidad del producto mediante análisis estático con SonarQube e ISO/IEC 25000 (SQuaRE).

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Framework | Spring Boot 3.2.4 |
| Lenguaje | Java 17 |
| Base de datos | PostgreSQL (JPA / Hibernate) |
| Seguridad | Spring Security + JWT (jjwt 0.11.5) |
| Imágenes | Cloudinary |
| Reportes | Apache POI (Excel) |
| Build | Maven (wrapper incluido: `mvnw` / `mvnw.cmd`) |
| Calidad | SonarQube, PMD, Checkstyle, SpotBugs, JaCoCo |

## Estado del proyecto

- **No está dockerizado.** Es una aplicación Spring Boot estándar que se ejecuta con el wrapper de Maven contra un PostgreSQL local.
- Docker se usa únicamente para la infraestructura de calidad (contenedores `sonarqube` y su base de datos), **no** para la aplicación.

## Requisitos previos

- **JDK 17** (o superior compatible con Spring Boot 3.2).
- **PostgreSQL 14+** corriendo en `localhost:5432`.
- (Opcional) **Docker** si quieres levantar SonarQube para análisis de calidad.

## Cómo levantarlo

1. Crear la base de datos en PostgreSQL (la aplicación usa `ddl-auto=update`, así que crea/actualiza tablas automáticamente):

   ```sql
   CREATE DATABASE bd_grass_sintetico;
   ```

2. Verificar la configuración de conexión en `src/main/resources/application.properties`.

3. Ejecutar el servidor:

   - **Windows:**
     ```powershell
     .\mvnw spring-boot:run
     ```
   - **Linux / macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```

El backend queda disponible en `http://localhost:8080`.

## Configuración

Todo vive en `src/main/resources/application.properties`:

| Parámetro | Valor por defecto |
|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/bd_grass_sintetico` |
| `spring.datasource.username` | `postgres` |
| `spring.datasource.password` | `admin123` |
| `server.port` | `8080` |
| `spring.servlet.multipart.max-file-size` | `10MB` |

> **Advertencia de seguridad:** las credenciales de la base de datos están en claro en el repositorio (contexto académico). En un entorno real deben externalizarse (variables de entorno o gestor de secretos) y nunca versionarse.

## Estructura del código

```
src/main/java/com/unas/grassbackend/
├── config/        # Configuraciones (CORS, seguridad, etc.)
├── controller/    # Endpoints REST (14 controladores)
├── dto/           # Objetos de transferencia de datos
├── entity/        # Entidades JPA (12 tablas)
├── exception/     # Manejo global de excepciones
├── mapper/        # Mapeo entidad ↔ DTO
├── repository/    # Interfaces Spring Data JPA
├── scheduler/     # Tareas programadas
├── security/      # Autenticación JWT y filtros
└── service/       # Lógica de negocio
```

### Módulos de negocio

Las 12 entidades y 14 controladores se agrupan en los siguientes módulos (usados también en el análisis de calidad):

| Módulo | Contenido | Entidades / Controladores principales |
|---|---|---|
| **Catálogo** | Sedes, canchas, productos | `Sede`, `Cancha`, `Producto` |
| **Reservas (Mód. 4)** | Reserva de canchas por clientes | `Reserva`, `PagoReserva` → `ReservaController` |
| **Ventas / POS (Mód. 5)** | Ventas, detalle y pagos | `Venta`, `DetalleVenta`, `PagoReserva` → `VentaController` |
| **Caja (Mód. 5)** | Turnos y movimientos de caja | `Caja`, `TurnoCaja`, `MovimientoCaja` → `CajaController`, `TurnoCajaController` |
| **Seguridad** | Usuarios, roles y autenticación | `Usuario`, `Rol` → `AuthController`, `UsuarioController`, `RolController` |
| **Reportes / Dashboard** | KPIs y exportación Excel | `DashboardController`, `ReporteExcelController`, `ConsultaController` |

## API principal

| Método / Ruta | Descripción |
|---|---|
| `POST /api/auth/login` | Autenticación y emisión de token JWT |
| `GET/POST/PUT/DELETE /api/canchas` | CRUD de canchas |
| `GET/POST/PUT/DELETE /api/reservas` | CRUD de reservas (Mód. 4) |
| `GET/POST/PUT/DELETE /api/ventas` | CRUD de ventas (Mód. 5) |
| `GET/POST/PUT/DELETE /api/cajas`, `/api/turnos-caja` | Control de caja (Mód. 5) |
| `GET /api/dashboard` | KPIs operativos |
| `GET /api/reportes/excel` | Exportación de reportes a Excel |

Los endpoints autenticados requieren cabecera `Authorization: Bearer <token>`.

## Estado de calidad (curso Calidad de Producto de Software)

Se realizó análisis estático con **SonarQube 26.7** (instancia local) sobre el proyecto `AnaliZelaya_Gestion_Grass_Sintetico`. Resumen de la última exploración:

### Calificaciones (modelo SQuaRE / ISO/IEC 25010)

| Característica | Calificación | Observación |
|---|---|---|
| Mantenibilidad (SQALE) | **C** (3.0) | Deuda técnica alta en el código existente |
| Seguridad | **E** (5.0) | Vulnerabilidades bloqueantes/críticas presentes |
| Fiabilidad | **D** (4.0) | Bugs detectados en el código |
| Quality Gate | **OK*** | *Solo evalúa New Code; el código existente queda fuera de la compuerta |

### Métricas globales

| Métrica | Valor |
|---|---|
| Líneas de código (NCLOC) | 3 445 en 72 archivos |
| Code smells | 1 626 |
| Deuda técnica | 15 590 minutos (≈ 260 horas) — **15.1 %** |
| Cobertura de pruebas | **4.9 %** (sin pruebas unitarias significativas) |
| Duplicación de código | 5.5 % |
| Vulnerabilidades | **13** (2 BLOCKER, 10 CRITICAL, 1 MINOR) |
| Bugs de fiabilidad | **8** |

### Concentración de deuda por módulo

La deuda no está distribuida de forma uniforme: los módulos con mayor riesgo son **Reservas (Mód. 4)** y **Pagos/POS/Caja (Mód. 5)**, que concentran el **45.3 %** de la deuda total en solo 31 de los 72 archivos:

| Módulo | Code smells | Deuda (min) | % deuda | Cobertura |
|---|---|---|---|---|
| Mód. 4 — Reservas | 337 | 3 908 | 25.1 % | 2.8 % |
| Mód. 5 — Pagos/POS/Caja | 401 | 3 160 | 20.3 % | 6.6 % |

### Dictamen

El informe técnico de calidad emite **NO APTO para despliegue en producción** hasta resolver al menos los hallazgos bloqueantes y críticos de seguridad, así como los bugs de fiabilidad detectados. Ver detalle en la documentación del curso.

## Análisis estático y CI/CD

### Ejecutar análisis local (SonarQube)

Con SonarQube corriendo en `http://localhost:9000` (por ejemplo, vía Docker):

```bash
mvn verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<token>
```

### Integración continua

El workflow [`.github/workflows/sonar.yml`](.github/workflows/sonar.yml) ejecuta en cada push a `main` y en cada pull request:

- Análisis de calidad con **SonarCloud**.
- **PMD**, **Checkstyle** y **SpotBugs** como pasos independientes.
- **JaCoCo** para reporte de cobertura.

### Herramientas de calidad configuradas en Maven

| Herramienta | Plugin | Propósito |
|---|---|---|
| PMD | `pmd-maven-plugin` 3.28.0 | Detección de malas prácticas |
| Checkstyle | `maven-checkstyle-plugin` 3.6.0 | Estilo y convenciones (reglas en `config/checkstyle/`) |
| SpotBugs | `spotbugs-maven-plugin` 4.10.5.0 | Detección de bugs potenciales |
| JaCoCo | `jacoco-maven-plugin` 0.8.13 | Cobertura de pruebas |
| Sonar | `sonar-maven-plugin` 5.8.0 | Análisis integral en SonarQube/SonarCloud |

## Documentación relacionada

- `docs/calc_scores.py` — Script de cálculo de scores de calidad (modelo ISO/IEC 25010).
- Informe técnico de análisis estático (EP4) — disponible en la carpeta del curso, fuera de este repositorio.
