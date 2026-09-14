# EF Quarkus - Sistema de Reservas

Backend REST para el control de reservas y disponibilidad de profesionales en un
centro de servicios (psicologia, mentorias, asesorias, tutorias). Trabajo final
del curso de Quarkus de MitoCode.

> **Estado**: completo respecto al enunciado. CRUD de Profesional y Cliente,
> Horarios Disponibles con validacion de solapamiento, Reserva (crear/cancelar
> con todas sus reglas de negocio), las 2 consultas de profesionales, coleccion
> de Postman, logs estructurados y SmallRye Fault Tolerance.

## Stack

- Quarkus 3.33 (LTS) sobre Java 25
- REST reactivo (`quarkus-rest` + `quarkus-rest-jackson`)
- Hibernate Reactive Panache + PostgreSQL (`quarkus-reactive-pg-client`)
- Flyway para migraciones (`quarkus-jdbc-postgresql` solo para esto, ya que
  Flyway no soporta drivers reactivos)
- Bean Validation (`quarkus-hibernate-validator`)
- SmallRye OpenAPI / Swagger UI
- SmallRye Fault Tolerance
- Logging JSON estructurado

## Arquitectura

DDD ligero, organizado por bounded context en vez de por capa tecnica global:

```
com.mitocode.{profesional,cliente,horario,reserva}
├── domain          entidades, value objects, reglas de negocio, puertos de repositorio
├── application     casos de uso / servicios de aplicacion
└── infrastructure  REST resources, DTOs, implementacion de repositorios (Panache)

com.mitocode.shared
├── domain          value objects compartidos entre contextos (RangoHorario)
├── exception       excepcion base + ExceptionMapper
└── config          configuracion transversal (OpenAPI, etc.)
```

Los agregados se referencian entre si por UUID (nunca por objeto completo), y
las reglas de negocio que cruzan agregados (ej. "cliente y profesional deben
estar activos" al crear una reserva) viven en la capa de aplicacion, no en el
dominio puro.

## Como correr el proyecto

Requisitos: Java 17+ (probado con Java 25) y Docker corriendo.

```shell script
./mvnw quarkus:dev
```

Quarkus Dev Services levanta un contenedor de PostgreSQL efimero solo y corre
las migraciones de Flyway automaticamente. No hace falta configurar nada mas
para desarrollo local.

Si preferis un Postgres persistente en vez de Dev Services:

```shell script
docker compose up -d
```

y las variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`
(ver `application.properties`, perfil `%prod`) apuntan por defecto a ese
contenedor.

### Tests

```shell script
./mvnw test
```

Los tests de endpoints (`@QuarkusTest` + RestAssured) tambien usan Dev
Services, asi que necesitan Docker disponible.

### Coleccion de Postman

`postman/EF_Quarkus_Reservas.postman_collection.json` cubre los 8 endpoints
principales, agrupados por contexto (Profesional, Cliente, HorarioDisponible,
Reserva). Las requests de creacion capturan automaticamente el id creado en
variables de coleccion (`profesionalId`, `clienteId`, `reservaId`) para
encadenar los siguientes pasos sin copiar/pegar UUIDs a mano. Importarla en
Postman e invocar contra `{{base_url}}` (por defecto `http://localhost:8080`).

### Empaquetado

```shell script
./mvnw package                                              # jar JVM
docker build -f src/main/docker/Dockerfile.jvm -t ef-quarkus-reservas .

./mvnw package -Dnative -Dquarkus.native.container-build=true   # nativo, sin GraalVM local
docker build -f src/main/docker/Dockerfile.native -t ef-quarkus-reservas-native .
```

## Decisiones tecnicas relevantes

- **Java 25** en vez de 17: es la version LTS mas reciente soportada por el
  stream de Quarkus usado, y ya era el `JAVA_HOME` por defecto del entorno de
  desarrollo.
- **`RangoHorario`** (value object en `shared.domain`) encapsula la logica de
  solapamiento/cobertura de intervalos horarios, reutilizada por
  `HorarioDisponible` y `Reserva` para no duplicar el algoritmo.
- **Sin `@Embeddable` para `RangoHorario`**: para evitar riesgos de
  compatibilidad entre records de Java y Hibernate Reactive, `horaInicio`/
  `horaFin` se guardan como columnas planas y `getRango()` es un getter
  derivado que arma el value object al vuelo.
- **`@WithSession` explicito** en todo endpoint que hace una lectura reactiva
  fuera de una transaccion (Panache/Hibernate Reactive no abre sesion sola en
  ese caso).
- **Deteccion de conflictos de unicidad** (ej. email duplicado de Cliente): se
  deja que la constraint `UNIQUE` de Postgres falle, y el repositorio traduce
  la excepcion (`org.hibernate.exception.ConstraintViolationException` con
  SQLState `23505`) a una excepcion de dominio propia, en vez de hacer un
  chequeo previo con condicion de carrera.
- **Excepciones propias** (`DominioException` y subtipos) cargan su propio
  `jakarta.ws.rs.core.Response.Status`, y un unico `ExceptionMapper` las
  traduce a JSON — no hay manejo de errores repetido por recurso.
- **TDD estricto**: cada pieza de dominio y cada endpoint se escribio primero
  como test en rojo, y recien despues la implementacion minima para ponerlo en
  verde.
- **Eliminar Profesional/Cliente es un soft-delete** (`DELETE` invoca el
  `desactivar()` de dominio y persiste), nunca un borrado fisico: `Reserva` y
  `HorarioDisponible` tienen FK hacia ambos, y la regla de negocio ya exige
  que cliente y profesional esten activos al crear una reserva. Un hard
  delete rompería el historial de reservas pasadas.
- **Consultas de profesionales** (`GET /profesionales` ordenado desc por
  reservas activas, `GET /reservas` agrupado por fecha) se resuelven
  cargando las colecciones completas via Panache y procesando el conteo/
  agrupamiento en memoria con `Stream`/`Collectors.groupingBy`, tal como pide
  el enunciado — no con una query SQL de agregacion. El orden desempata de
  forma deterministica por apellidos/nombres/id cuando dos profesionales
  tienen la misma cantidad de reservas activas.
- **`@Timeout` de SmallRye Fault Tolerance protege una lectura
  (`ProfesionalService.listarOrdenadosPorReservasActivas`), nunca la
  escritura de `ReservaService.crear`**: para un metodo que retorna `Uni`,
  el timeout cancela la *suscripcion* del lado cliente al expirar, pero el
  driver reactivo de Postgres no soporta cancelar una escritura ya enviada
  al servidor. Protegiendo una escritura, un timeout podria devolver error
  al cliente mientras la reserva se crea igual en la base ("reserva
  fantasma"). Protegiendo una lectura, un timeout nunca deja estado a medio
  escribir. `TimeoutExceptionMapper` traduce el `TimeoutException` resultante
  a un 503 explicito (con log de advertencia) en vez de dejar que caiga al
  500 generico de Quarkus. El valor del timeout no esta hardcodeado: se
  externaliza en `application.properties` con la clave estandar de
  MicroProfile Fault Tolerance
  (`.../listarOrdenadosPorReservasActivas/Timeout/value`), overridable sin
  recompilar.
- **Logs estructurados**: `LoggingFilter` (JAX-RS `ContainerRequestFilter` +
  `ContainerResponseFilter`) loguea entrada y salida de cada request,
  adjuntando `httpMethod`/`httpPath`/`httpStatus`/`durationMs` via MDC antes
  de la linea de salida — Quarkus los serializa como JSON estructurado en
  produccion (`quarkus.log.console.json=true`).
