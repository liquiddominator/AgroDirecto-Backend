# AgroDirecto

AgroDirecto es un backend Java con Spring Boot orientado a una plataforma de comercio agropecuario directo. La estructura actual separa autenticacion, seguridad, usuarios, roles, auditoria y modulos de negocio como productos, clientes y pedidos.

## Stack tecnico

| Componente | Uso |
| --- | --- |
| Java 21 | Version de Java definida en `pom.xml`. |
| Spring Boot 4.0.6 | Framework principal de la aplicacion. |
| Maven Wrapper | Ejecucion reproducible de Maven sin depender de una instalacion global. |
| Spring Web MVC | Creacion de controladores y endpoints HTTP. |
| Spring Data JPA | Persistencia y acceso a datos relacionales. |
| PostgreSQL | Motor de base de datos previsto por el driver incluido. |
| Flyway | Versionado y ejecucion de migraciones SQL. |
| Spring Security | Autenticacion, autorizacion y filtros de seguridad. |
| Bean Validation | Validacion de datos de entrada. |
| Lombok | Reduccion de codigo repetitivo en modelos y servicios. |

## Requisitos

- JDK 21 instalado y disponible en `JAVA_HOME`.
- Docker Desktop o Docker Engine con Docker Compose.
- PostgreSQL local solo es necesario si decides no usar Docker.
- Acceso a terminal desde la raiz del proyecto.

La raiz del proyecto es:

```powershell
C:\Users\Liquido\Desktop\AgroDirecto
```

## Configuracion de base de datos

El proyecto usa PostgreSQL, JPA y Flyway. La configuracion por defecto apunta a una base local en `localhost:5432` con estos valores:

| Variable | Valor por defecto |
| --- | --- |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/agro_directo` |
| `SPRING_DATASOURCE_USERNAME` | `agrodirecto` |
| `SPRING_DATASOURCE_PASSWORD` | `agrodirecto` |
| `DB_SCHEMA` | `agro_directo` |

Las migraciones estan en:

```text
src/main/resources/db/migration/
```

Flyway las ejecuta automaticamente al iniciar la aplicacion.

## Ejecucion con Docker

Para levantar PostgreSQL y la aplicacion juntos:

```powershell
docker compose up --build
```

La API queda disponible en:

```text
http://localhost:8080
```

Swagger UI queda disponible en:

```text
http://localhost:8080/swagger-ui.html
```

La especificacion OpenAPI queda disponible en:

```text
http://localhost:8080/v3/api-docs
```

La base de datos queda disponible desde tu maquina en:

```text
localhost:5432
```

Credenciales de desarrollo:

```text
Base de datos: agro_directo
Usuario: agrodirecto
Password: agrodirecto
Schema: agro_directo
```

Usuario administrador inicial creado por Flyway:

```text
Email: admin@agrodirecto.local
Password: 123456
Rol: ADMIN
```

Para levantar solo la base de datos en Docker y ejecutar la app localmente con Maven:

```powershell
docker compose up -d db
.\mvnw.cmd spring-boot:run
```

Para detener los contenedores:

```powershell
docker compose down
```

Para detenerlos y borrar el volumen de datos de PostgreSQL:

```powershell
docker compose down -v
```

Usa `down -v` solo cuando quieras reiniciar la base desde cero y volver a aplicar todas las migraciones.

## Configuracion manual sin Docker

Si no usas Docker, crea una base PostgreSQL llamada `agro_directo` y configura las credenciales.

Ejemplo usando variables de entorno en PowerShell:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/agro_directo"
$env:SPRING_DATASOURCE_USERNAME="agrodirecto"
$env:SPRING_DATASOURCE_PASSWORD="agrodirecto"
```

Ejemplo equivalente en `src/main/resources/application-local.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/agro_directo
spring.datasource.username=agrodirecto
spring.datasource.password=agrodirecto
spring.jpa.hibernate.ddl-auto=validate
spring.flyway.enabled=true
spring.flyway.schemas=agro_directo
```

Si usas un archivo `application-local.properties`, ejecuta la aplicacion con el perfil `local`:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=local
```

El modelo SQL de referencia esta en:

```text
docs/db/agro-directo-db.txt
```

Ese archivo es una referencia monolitica. La ejecucion real del esquema se hace mediante migraciones Flyway versionadas, con un archivo de creacion por tabla.

## Como ejecutar el proyecto localmente

Primero asegurate de tener PostgreSQL disponible. La forma recomendada es levantar solo el servicio de base de datos con Docker:

```powershell
docker compose up -d db
```

Luego, desde la raiz del repositorio, en Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

Por defecto, Spring Boot levanta la aplicacion en:

```text
http://localhost:8080
```

## Endpoints de autenticacion

Los endpoints principales de autenticacion estan bajo `/api/auth`.

| Metodo | Ruta | Autenticacion | Descripcion |
| --- | --- | --- | --- |
| `POST` | `/api/auth/register` | Publica | Registra usuario multitipo y devuelve tokens. |
| `POST` | `/api/auth/login` | Publica | Inicia sesion y devuelve tokens. |
| `POST` | `/api/auth/refresh` | Publica | Rota el refresh token y devuelve un nuevo par de tokens. |
| `POST` | `/api/auth/logout` | Publica | Revoca el refresh token enviado. |
| `GET` | `/api/auth/me` | Bearer JWT | Devuelve el usuario autenticado. |

`register` y `login` devuelven:

```json
{
  "tokenType": "Bearer",
  "accessToken": "...",
  "accessTokenExpiresAt": "2026-04-27T20:00:00Z",
  "refreshToken": "...",
  "refreshTokenExpiresAt": "2026-05-27T20:00:00Z",
  "user": {
    "id": 1,
    "email": "usuario@example.com",
    "fullName": "Usuario Demo",
    "status": "REGISTERED",
    "primaryRole": "BUYER",
    "roles": ["BUYER"]
  }
}
```

Ejemplo de `login`:

```json
{
  "email": "admin@agrodirecto.local",
  "password": "123456"
}
```

Ejemplo de `refresh` y `logout`:

```json
{
  "refreshToken": "..."
}
```

## Como ejecutar pruebas

Primero levanta la base de datos:

```powershell
docker compose up -d db
```

En Windows:

```powershell
.\mvnw.cmd test
```

En Linux o macOS:

```bash
./mvnw test
```

La prueba actual verifica que el contexto de Spring Boot cargue correctamente.

## Como generar el artefacto

Para generar el artefacto ejecutando pruebas, manten la base de datos activa:

En Windows:

```powershell
.\mvnw.cmd clean package
```

En Linux o macOS:

```bash
./mvnw clean package
```

Si solo necesitas construir el `.jar` sin ejecutar pruebas:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Luego puedes ejecutar el `.jar` generado:

```powershell
java -jar target/AgroDirecto-0.0.1-SNAPSHOT.jar
```

## Descripcion tecnica general

El punto de entrada es `com.agrodirecto.AgroDirectoApplication`, anotado con `@SpringBootApplication`. Desde ahi Spring Boot inicializa el contexto, escanea componentes bajo `com.agrodirecto` y habilita la configuracion automatica segun las dependencias del proyecto.

La organizacion esta pensada por dominios y capas:

| Paquete | Responsabilidad |
| --- | --- |
| `auth` | Flujo de autenticacion, registro, login y tokens. |
| `security` | Configuracion de Spring Security, filtros HTTP y soporte JWT. |
| `user` | Gestion de usuarios. |
| `role` | Roles, permisos y autorizacion. |
| `business/product` | Productos agropecuarios publicados en la plataforma. |
| `business/customer` | Clientes o compradores del sistema. |
| `business/order` | Pedidos, items y flujo de compra. |
| `audit` | Registro de acciones y trazabilidad. |
| `common` | Configuracion transversal, excepciones, respuestas y utilidades compartidas. |

Cada modulo sigue una separacion por capas:

| Carpeta | Responsabilidad |
| --- | --- |
| `controller` | Endpoints REST y entrada HTTP. |
| `dto` | Contratos de entrada y salida. |
| `entity` | Entidades persistentes de JPA. |
| `mapper` | Conversion entre entidades y DTOs. |
| `repository` | Acceso a datos con Spring Data JPA. |
| `service` | Casos de uso y logica de negocio. |

El diseno de base de datos documentado contempla usuarios, roles, permisos, perfiles por rol, productos, imagenes, precios de referencia, carritos, pedidos, pagos, envios, confirmaciones de entrega, calificaciones, notificaciones y auditoria.

## Estado actual del proyecto

El repositorio esta en una etapa inicial de estructura. Ya existen las carpetas base, dependencias principales, punto de entrada de Spring Boot, prueba de carga de contexto, configuracion Docker, migraciones Flyway y documentacion tecnica. La mayoria de modulos todavia contienen archivos `.gitkeep`, por lo que faltan implementar entidades, repositorios, servicios y controladores.
