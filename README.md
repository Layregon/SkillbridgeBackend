# SkillBridge — Backend

API REST de **SkillBridge**, una aplicación para gestionar la capacidad y las competencias técnicas de un equipo: profesionales, competencias, certificaciones, servicios y asignaciones.

Este repositorio contiene **solo el backend**. El frontend (Angular), la analítica (FastAPI) y los scripts de base de datos viven en el proyecto completo.

## Tecnología

| Componente | Versión |
|---|---|
| Java | 25 |
| Spring Boot | 4.0.7 |
| Persistencia | Spring Data JPA / Hibernate + MySQL 8 |
| Seguridad | Spring Security + JWT (jjwt 0.13.0) |
| Documentación de la API | springdoc-openapi 3.0.0 (Swagger UI) |
| Utilidades | Lombok, Jakarta Validation, DevTools |
| Tests | Spring Boot Test + H2 (modo MySQL) |

Sigue el patrón de los ejercicios de clase: `modelo → repositorio → controlador`, **sin capa `Service`**. Los controladores llaman directamente a los `JpaRepository`. Las relaciones N:M con datos propios son entidades (`ProfesionalCompetencia` y `Asignacion`), no un `@ManyToMany` automático.

## Estructura

```text
src/main/java/com/inserta/gestioncompetencias/
├── GestionCompetenciasApplication.java
├── modelo/          9 entidades JPA con Javadoc (8 de negocio + Usuario)
├── repositorio/     9 repositorios Spring Data
├── controlador/     10 controladores REST + ApiExceptionHandler
├── dto/             5 records (login, capacidad, competencia en riesgo)
├── config/          SecurityConfig, JwtFilter, JwtService, UsuarioDetailsService,
│                    SwaggerConfig, SpaConfig
└── carga/           DatosIniciales (datos demo al arrancar)
src/main/resources/  application.properties, static/ (aquí va Angular compilado)
src/test/            contexto de Spring con H2
```

## Requisitos

- JDK 25
- MySQL 8 en `localhost:3306`

No hace falta crear la base de datos a mano: la URL lleva `createDatabaseIfNotExist=true`, así que `gestion_competencias` se crea sola y Hibernate genera las tablas (`ddl-auto=update`).

## Arranque

```bash
./mvnw clean spring-boot:run
```

- API: `http://localhost:8080/api/...`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

> ⚠️ **`./mvnw` no funciona en este repositorio tal como está.** Falta `.mvn/wrapper/maven-wrapper.properties` y el script termina con *cannot open ./.mvn/wrapper/maven-wrapper.properties*. Dos salidas: usar un Maven instalado (`mvn clean spring-boot:run`) o regenerar el wrapper con `mvn wrapper:wrapper` y versionar la carpeta `.mvn/`.

## Configuración

Todo se controla con variables de entorno; los valores por defecto sirven para desarrollo.

| Variable | Por defecto | Para qué |
|---|---|---|
| `DB_USER` | `root` | Usuario de MySQL |
| `DB_PASSWORD` | `root` | Contraseña de MySQL |
| `DDL_AUTO` | `update` | Gestión del esquema por Hibernate. En un entorno real usar `validate` o `none` |
| `DATOS_INICIALES` | `true` | Carga datos demo al arrancar si la base está vacía |
| `JWT_SECRET` | clave de demostración | Clave de firma del JWT. **Sustituirla en un entorno real** |
| `JWT_EXPIRACION_MS` | `28800000` | Caducidad del token (8 horas) |

La zona horaria de Hibernate es `Europe/Madrid`.

## Usuarios de demostración

`DatosIniciales` crea dos usuarios y datos de ejemplo (6 profesionales, 7 competencias, 3 servicios y 7 asignaciones). Si ya has importado el SQL completo del proyecto, detecta que hay datos y no los duplica.

| Usuario | Contraseña | Rol |
|---|---|---|
| `admin` | `admin123` | `ADMIN` |
| `usuario` | `usuario123` | `USER` |

El nombre de usuario **distingue mayúsculas de minúsculas** (`admin` no es `Admin`): la consulta de login usa `BINARY` en MySQL. Un usuario con `activo = false` no puede entrar.

## Seguridad

1. `POST /api/auth/login` con `{"username": "...", "password": "..."}` devuelve `{token, username, rol}`.
2. El resto de peticiones envían `Authorization: Bearer <token>`.

| Ruta | Acceso |
|---|---|
| `/api/auth/**`, Swagger y OpenAPI | Público |
| `DELETE /api/**` | Solo `ADMIN` |
| Resto de `/api/**` | Usuario autenticado |

Sesión sin estado (sin cookies) y CSRF desactivado. **CORS** admite únicamente el origen `http://localhost:4200`; si el frontend se sirve desde otro origen hay que cambiarlo en `SecurityConfig`. Con el frontend dentro del `.jar` (mismo origen) no hace falta.

En Swagger UI, pulsa **Authorize** e introduce el token para probar los endpoints protegidos.

## API

Todos los recursos siguen el mismo patrón: `GET /consultar`, `GET /consultar/{id}`, `POST /crear`, `PUT /actualizar/{id}`, `DELETE /borrar/{id}`. Los que no lo cumplen del todo se indican.

| Recurso | Ruta base | Extras |
|---|---|---|
| Perfiles | `/api/perfiles` | |
| Sedes | `/api/sedes` | |
| Competencias | `/api/competencias` | `GET /consultar/area`, `GET /riesgo?umbral=1` |
| Profesionales | `/api/profesionales` | `GET /filtrar` (nombre, sedeId, perfilId) |
| Competencias de un profesional | `/api/profesional-competencias` | `GET /consultar/profesional/{id}` (sin consulta por id) |
| Certificaciones | `/api/certificaciones` | `GET /proximas?dias=60`, `GET /caducadas`, `GET /consultar/profesional/{id}` (sin consulta por id) |
| Servicios | `/api/servicios` | |
| Asignaciones | `/api/asignaciones` | `GET /consultar/profesional/{id}`, `GET /consultar/servicio/{id}` (sin consulta por id) |
| Capacidad | `/api/capacidad` | Solo lectura: `GET /consultar`, `GET /resumen` |
| Autenticación | `/api/auth` | `POST /login` |

## Reglas de negocio implementadas

- **Ocupación** de un profesional = suma de la dedicación de sus asignaciones **activas**. Estados: `SIN_ASIGNAR` (0), `DISPONIBLE` (< 100), `COMPLETO` (= 100) y `SOBRECARGADO` (> 100).
- Competencia **en riesgo**: tiene un número de profesionales con la competencia *verificada* igual o inferior al umbral (por defecto 1). El umbral no puede ser negativo.
- No se puede repetir la pareja profesional-competencia → `409`.
- No puede haber dos asignaciones **activas** del mismo profesional al mismo servicio → `409`.
- Borrados en cascada hechos a mano, en una transacción: al borrar un **profesional** se borran antes sus competencias, certificaciones y asignaciones; al borrar un **servicio**, sus asignaciones; al borrar una **competencia**, sus relaciones con profesionales.
- Validación con Jakarta Validation en las entidades (`@NotBlank`, `@Size`, `@Pattern`, rangos).

## Manejo de errores

`ApiExceptionHandler` traduce las excepciones a respuestas JSON:

| Situación | Código |
|---|---|
| Datos no válidos (`@Valid`) | `400` con `mensaje` (el primero) y `campos` (todos) |
| Argumento no válido (p. ej. umbral negativo) | `400` |
| Duplicado o dato relacionado (`DataIntegrityViolation`, `ResponseStatusException`) | `409` / el código indicado |
| Registro no encontrado (`RuntimeException`) | `404` |
| Sin token o token caducado | `401` |
| Sin permisos (borrar sin ser `ADMIN`) | `403` |

## Tests

```bash
mvn test
```

Hay una única prueba de arranque (`contextLoads`) que levanta el contexto de Spring contra **H2 en memoria en modo MySQL**, con la carga demo desactivada y su propia clave JWT (`src/test/resources/application.properties`). No cubre controladores ni reglas de negocio.

## Un solo `.jar` con Angular

En el proyecto completo, `desplegar.sh` compila Angular y copia el resultado a `src/main/resources/static/`. `SpaConfig` reenvía las rutas de Angular a `index.html`, de modo que recargar con F5 en `/profesionales` no da 404. Después:

```bash
mvn clean package
java -jar target/skillbridge-0.0.1-SNAPSHOT.jar
```

La base de datos no va dentro del `.jar`.

## Nota sobre nombres

La marca es **SkillBridge**. Se mantiene el paquete Java `com.inserta.gestioncompetencias` para no hacer un renombrado técnico que no aporta funcionalidad.
