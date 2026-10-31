# atlas-assetflow-services

Backend de un CMMS (sistema de gestión de mantenimiento): Gradle multi-módulo, Spring Boot 3.3,
MongoDB y RabbitMQ, con el dominio modelado siguiendo DDD táctico.
Frontend en el repo `atlas-assetflow-client`.

## Módulos

- `shared-kernel` — dominio puro compartido entre bounded contexts: jerarquía de excepciones de
  negocio, evento de dominio y raíz de agregado, puerto de reloj y paginación. Sin dependencias de
  Spring ni de la base de datos.
- `activos-service` — bounded context **Activos**: inventario de equipos, su estado y su baja.

## Arranque local

```
# La infraestructura vive en ../platform (ver ../platform/README.md):
#   cd ..\platform ; .\scripts\up.ps1 -Consoles
./gradlew :activos-service:bootRun         # http://localhost:8091/actuator/health
./gradlew build                            # compila y corre tests
```

## Convenciones

- **Errores HTTP:** `ProblemDetail` (RFC 7807) con un campo `codigo` estable por error de negocio.
  400 dato de entrada inválido, 404 agregado inexistente, 409 invariante de negocio violada.
- **Paginación:** `?pagina=0&tamano=20`, base cero, tope 200. Respuesta
  `{ contenido, pagina, tamano, totalElementos }`.
- **Fechas:** `Instant` en el dominio; la conversión a zona local ocurre en la capa web.
- **Dominio sin infraestructura:** los agregados no llevan anotaciones de persistencia, y los
  documentos de Mongo son un modelo aparte.

## Estado

Bounded context Activos en curso: dominio y puertos definidos, adaptador de persistencia y capa web
pendientes.
