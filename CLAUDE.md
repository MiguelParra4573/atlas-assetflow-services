# CLAUDE.md

Backend de Atlas AssetFlow (CMMS): Gradle multi-módulo, Spring Boot 3.3.4, MongoDB, con el dominio
modelado en DDD táctico y la estructura de cada módulo en hexagonal (puertos y adaptadores). Frontend
en el repo hermano `atlas-assetflow-client`. Infraestructura local en `../platform/`.

## Antes de tocar código

- Arquitectura y stack: `../docs/arquitectura.md` (raíz del workspace). **DDD + hexagonal**, no
  Clean Architecture ni microservicios por capas de servicio: ver esa sección 2 para el por qué.
- Cómo se escribe el código de este repo, y qué gate lo rompe si no se cumple: `convenciones.md`
  (este directorio).
- En qué orden se construye: `../docs/plan.md`.
- Último estado de la sesión: `.claude/checkpoints/INDEX.md`.

## Arranque local

```
# La infraestructura vive en ../platform (ver ../platform/README.md):
#   cd ..\platform ; .\scripts\up.ps1 -Consoles
./gradlew :activos-service:bootRun    # http://localhost:8091/actuator/health
./gradlew build                       # compila, formatea, analiza y corre tests
```

## El harness de calidad

`./gradlew build` no solo compila: aplica Spotless (formato), Error Prone y NullAway (análisis
estático), ArchUnit (fronteras del hexágono y convenciones de código) y genera el reporte de
JaCoCo. Si falla, `convenciones.md` dice qué gate fue y cómo se arregla — léelo antes de silenciar
algo con un `@SuppressWarnings` nuevo o un escape.

No se agrega una dependencia, una anotación o un patrón que las reglas de `ArquitecturaTests` o
`ConvencionesDeCodigoTests` (`activos-service/src/test/.../activos/`) ya prohíben, aunque compile:
si una regla molesta, se discute y se cambia, no se rodea.
