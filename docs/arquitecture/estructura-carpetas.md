# Estructura de carpetas

Este documento describe la organizacion prevista de los directorios principales del proyecto AgroDirecto. Los archivos de soporte para mantener carpetas vacias bajo control de versiones no se listan aqui.

```text
AgroDirecto/
├── docs/
├── scripts/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── agrodirecto/
│   │   │           ├── audit/
│   │   │           │   ├── entity/
│   │   │           │   ├── repository/
│   │   │           │   └── service/
│   │   │           ├── auth/
│   │   │           │   ├── controller/
│   │   │           │   ├── dto/
│   │   │           │   ├── mapper/
│   │   │           │   └── service/
│   │   │           ├── business/
│   │   │           │   ├── customer/
│   │   │           │   │   ├── controller/
│   │   │           │   │   ├── dto/
│   │   │           │   │   ├── entity/
│   │   │           │   │   ├── mapper/
│   │   │           │   │   ├── repository/
│   │   │           │   │   └── service/
│   │   │           │   ├── order/
│   │   │           │   │   ├── controller/
│   │   │           │   │   ├── dto/
│   │   │           │   │   ├── entity/
│   │   │           │   │   ├── mapper/
│   │   │           │   │   ├── repository/
│   │   │           │   │   └── service/
│   │   │           │   └── product/
│   │   │           │       ├── controller/
│   │   │           │       ├── dto/
│   │   │           │       ├── entity/
│   │   │           │       ├── mapper/
│   │   │           │       ├── repository/
│   │   │           │       └── service/
│   │   │           ├── common/
│   │   │           │   ├── config/
│   │   │           │   ├── exception/
│   │   │           │   ├── response/
│   │   │           │   └── util/
│   │   │           ├── role/
│   │   │           │   ├── dto/
│   │   │           │   ├── entity/
│   │   │           │   ├── repository/
│   │   │           │   └── service/
│   │   │           ├── security/
│   │   │           │   ├── config/
│   │   │           │   ├── filter/
│   │   │           │   └── jwt/
│   │   │           └── user/
│   │   │               ├── controller/
│   │   │               ├── dto/
│   │   │               ├── entity/
│   │   │               ├── mapper/
│   │   │               ├── repository/
│   │   │               └── service/
│   │   └── resources/
│   │       ├── db/
│   │       │   └── migration/
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/
│               └── agrodirecto/
├── .dockerignore
├── .gitattributes
├── .gitignore
├── docker-compose.yml
├── Dockerfile
├── HELP.md
├── mvnw
├── mvnw.cmd
├── pom.xml
```

## Directorios principales

| Directorio | Uso previsto |
| --- | --- |
| `docs/` | Documentacion tecnica y funcional del proyecto. |
| `scripts/` | Scripts auxiliares para automatizacion, mantenimiento, carga de datos o tareas operativas. |
| `src/main/java/com/agrodirecto/` | Paquete raiz del codigo fuente Java de la aplicacion. |
| `src/main/resources/` | Recursos de configuracion y archivos usados por Spring Boot en tiempo de ejecucion. |
| `src/test/java/com/agrodirecto/` | Pruebas automatizadas del proyecto. |

## Modulos de aplicacion

| Directorio | Uso previsto |
| --- | --- |
| `auth/` | Login, registro, refresh token si se usa y respuestas propias de autenticacion. |
| `security/` | Configuracion de Spring Security, filtros JWT, generacion y validacion de tokens, y reglas de acceso. |
| `user/` | Gestion de usuarios de la aplicacion. |
| `role/` | Gestion de roles, permisos y relaciones de autorizacion. |
| `business/` | Modulos principales del negocio. Aqui se ubican dominios reales como productos, ventas, compras, inventario, clientes y proveedores. |
| `common/` | Codigo transversal compartido: excepciones globales, respuestas estandar, utilidades y configuraciones comunes. |
| `audit/` | Trazabilidad de acciones sobre datos: quien creo, modifico o elimino registros. |

## Convenciones internas de modulo

| Directorio | Uso previsto |
| --- | --- |
| `controller/` | Controladores REST y puntos de entrada HTTP. |
| `dto/` | Objetos de transferencia de datos para requests, responses y contratos externos. |
| `entity/` | Entidades persistentes del dominio. |
| `mapper/` | Conversiones entre entidades, DTOs y otros modelos internos. |
| `repository/` | Acceso a datos, normalmente mediante Spring Data JPA. |
| `service/` | Logica de negocio y casos de uso del modulo. |
| `config/` | Configuraciones especificas del modulo o configuraciones compartidas. |
| `exception/` | Excepciones personalizadas y manejo global de errores. |
| `response/` | Estructuras de respuesta estandar para la API. |
| `util/` | Utilidades reutilizables sin estado de negocio propio. |
| `filter/` | Filtros HTTP o de seguridad, como filtros JWT. |
| `jwt/` | Componentes para generar, firmar, validar y leer tokens JWT. |

## Recursos

| Directorio | Uso previsto |
| --- | --- |
| `src/main/resources/db/migration/` | Migraciones de base de datos, por ejemplo scripts versionados para Flyway. |
| `src/main/resources/static/` | Archivos estaticos servidos por Spring Boot si la aplicacion los necesita. |
| `src/main/resources/templates/` | Plantillas del servidor si se incorpora renderizado con motor de templates. |