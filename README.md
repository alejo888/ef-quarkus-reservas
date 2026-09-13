# EF Quarkus - Sistema de Reservas

Backend REST para el control de reservas y disponibilidad de profesionales en un
centro de servicios (psicologia, mentorias, asesorias, tutorias). Trabajo final
del curso de Quarkus de MitoCode.

> **Estado**: en desarrollo. Implementado hasta ahora: CRUD de Profesional y
> Cliente, registro de Horarios Disponibles con validacion de solapamiento.
> Pendiente: Reserva (crear/cancelar/consultas) y coleccion de Postman.

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
  `HorarioDisponible` y (mas adelante) `Reserva` para no duplicar el
  algoritmo.
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
