# atlas-assetflow-services

Backend del CMMS Atlas AssetFlow: Gradle multi-módulo, Spring Boot 3.3, MongoDB y RabbitMQ.
Frontend en el repo `atlas-assetflow-client`.

- Plan por fases: `plan-cmms.md`
- Diseño de dominio: `docs/diseno-dominio.md`

## Arranque local

```
docker compose up -d                       # MongoDB + RabbitMQ (UI en :15672, atlas/atlas)
./gradlew :activos-service:bootRun         # http://localhost:8091/actuator/health
./gradlew build                            # compila y corre tests
```

## Estado

Fases 0 y 1 completas. Fase 2 (agregado `Activo`) con esqueleto listo en `activos-service`.
