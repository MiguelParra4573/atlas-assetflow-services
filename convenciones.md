# Convenciones de código — atlas-assetflow-services

Cómo se escribe el código de este repo y qué lo verifica. El **por qué** de cada elección está en
`../docs/adr/0001-harness-de-calidad.md` (raíz del workspace); acá está el **cómo**.

La arquitectura —qué depende de qué— es otro documento: `../docs/arquitectura.md` en la raíz del
workspace, que sirve a los dos repos.

## Puesta a punto

Una vez por clon:

```bash
git config core.hooksPath .githooks                      # activa los hooks de git
git config blame.ignoreRevsFile .git-blame-ignore-revs    # saca el reformateo del blame
```

La primera también la deja hecha `./gradlew build` sola, con la tarea `activarHooks`: no es
estrictamente necesario correr el primer comando a mano.

Hace falta **JDK 17** —no 21, no 25: Spotless sirve un google-java-format distinto según la JVM
(1.28.0 en JVM 17, 1.30.0 en 21+), y el harness se validó con 1.28.0. Y **Podman o Docker
corriendo** para los tests de integración con Testcontainers, cuando existan (hoy `activos-service`
todavía no tiene adaptador Mongo).

## Los gates que rompen el build

| Gate | Tarea de Gradle | Qué verifica | Cómo se arregla |
| --- | --- | --- | --- |
| Spotless | `spotlessCheck` | Formato de todo `src/**/*.java` | `./gradlew spotlessApply` |
| `-Werror` (javac) | `compileJava` | Cualquier warning de javac | Arreglar el warning; como último recurso, el escape de abajo |
| Error Prone | `compileJava` | Bugs verificables (`ObjectToString`, `ReturnValueIgnored`, `EqualsHashCode`…) | Arreglar; el mensaje suele proponer el parche |
| NullAway | `compileJava` | Nulos en los paquetes `@NullMarked` | Anotar `@Nullable` lo que de verdad puede ser nulo, o corregir el flujo |
| ArchUnit | `test` | Fronteras hexagonales y convenciones de código | Ver los dos archivos de reglas, abajo |
| JaCoCo | `jacocoTestReport` | Genera el reporte. **Sin umbral todavía**, ver § Cobertura | — |

`./gradlew build` corre todo. `./gradlew check` corre los gates sin empaquetar. El `pre-commit`
corre solo `spotlessCheck`, que es el único que tarda milisegundos; el resto es trabajo de CI.

## Formato

**google-java-format en modo AOSP**: 4 espacios de bloque, 8 de continuación, 100 columnas. No se
discute el estilo; lo aplica Spotless.

```bash
./gradlew spotlessApply    # formatea
./gradlew spotlessCheck    # verifica (lo corre check/build)
```

El `.editorconfig` declara el mismo ancho de 100 columnas, así que el IDE y el formateador dicen lo
mismo.

No hay pasos de `importOrder` ni `removeUnusedImports`: los hace el propio google-java-format, y
dos motores sobre el mismo archivo se pelean en cada `apply`. El markdown **no** se formatea: lo
gobierna el `.editorconfig` y nada más.

Un commit de reformateo masivo **no lleva ningún otro cambio, nunca**, y su SHA va a
`.git-blame-ignore-revs`.

## Nulabilidad

Se usa **JSpecify**, como `compileOnly`: Spring Boot 3.3.4 es Framework 6.1 y usa
`org.springframework.lang.Nullable`, **no** JSpecify todavía (eso llega con Framework 7 / Boot 4).
JSpecify acá es solo para anotar el dominio propio; no interactúa con ninguna firma de Spring.

El alcance de NullAway lo declara el código, no el `build.gradle.kts`: un `package-info.java` por
paquete que se quiera analizar.

```java
@org.jspecify.annotations.NullMarked
package com.atlas.assetflow.activos.domain;
```

Dentro de un paquete `@NullMarked`, todo es no-nulo salvo lo que se marque. En un `record` se anota
el componente:

```java
public record TransicionEstado(
        @Nullable EstadoActivo estadoAnterior,   // null en el registro inicial
        EstadoActivo estadoNuevo,
        Instant ocurridaEn,
        String motivo) {
```

Funciona porque `org.jspecify.annotations.Nullable` es `TYPE_USE` y en un componente de record se
propaga al campo, al accesor y al parámetro del constructor canónico, los tres a la vez.

**Esto no cambia el comportamiento en runtime.** Es documentación que el compilador verifica, nada
más: no se ganó ninguna validación de entrada.

Paquetes anotados hoy: `activos.domain`, `shared.domain`. Se anota `..application..` cuando exista.
Los adaptadores (`infrastructure`) se dejan sin anotar a propósito —el futuro adaptador Mongo, los
converters— porque ahí NullAway produce falsos positivos. Error Prone, en cambio, **sí** corre en
los adaptadores: es donde viven los bugs que atrapa.

## Cobertura

```bash
./gradlew jacocoTestReport    # genera build/reports/jacoco/test/html/index.html por módulo
```

**Sin umbral todavía, a propósito.** `Activo`, `CodigoActivo` y `EstadoActivo` no tienen cuerpo
(Fase 2, TODO en cada clase) y `ActivoTest` tiene sus 4 casos con `@Disabled` a nivel de clase.
Medir hoy pinearía un número sobre código que no existe.

**Procedimiento al cerrar la Fase 2** (cuando esos `@Disabled` se quiten): correr
`jacocoTestReport`, leer el reporte, y fijar el umbral con una verificación de JaCoCo (`line` y
`branch`) **redondeado hacia abajo al 0,05**. No al 0,01: un umbral exacto rompe el build con un
refactor de tres líneas, y entonces lo primero que se aprende del harness es a bajarle el número.

**El umbral solo sube.** Subirlo es parte del cierre de cada fase del plan. Bajarlo pide una
entrada en `../docs/bitacora.md` con la razón. `BRANCH` importa más que `LINE` para reglas de
negocio: una transición de estado con cuatro ramas cubierta en una línea de test da `LINE` alto y
`BRANCH` bajo, y `BRANCH` es la verdad.

## Las reglas verificadas

Dos archivos en `activos-service/src/test/.../activos/`, con dos propósitos distintos:

| Archivo | Qué gobierna | Documento |
| --- | --- | --- |
| `ArquitecturaTests.java` | Dependencias entre capas del hexágono | `../docs/arquitectura.md` § 2.3 |
| `ConvencionesDeCodigoTests.java` | Buenas prácticas dentro de una clase | este documento |

Los dos importan desde `ClasesDelProyecto`, que usa `DO_NOT_INCLUDE_TESTS`: analizan **solo
`src/main`**. Los tests usan a propósito cosas que las reglas prohíben (mocks del repositorio fuera
de `infrastructure`, por ejemplo), y eso es legítimo. Ese invariante es lo que hace seguras las
reglas.

Lo que verifica cada archivo:

**`ArquitecturaTests`**
- `..domain..` no depende de `..infrastructure..` ni de `..application..`.
- `..domain..` no depende de `org.springframework..`, `org.bson..` ni `com.mongodb..`.
- `shared-kernel` no depende de nada de Spring ni de Mongo (su propio `build.gradle.kts` ya lo
  garantiza; la regla lo hace visible y lo mantiene así).
- `domain` ← `application` ← `infrastructure`, como arquitectura por capas de ArchUnit.

**`ConvencionesDeCodigoTests`**
- **`Instant.now()`, `LocalDate.now()` y `System.currentTimeMillis()` prohibidos en `..domain..`**
  — el instante entra por el puerto `Reloj`. Hace ejecutable la convención que antes era solo
  prosa en `docs/arquitectura.md` § 5.
- **Anotaciones de persistencia de Spring Data prohibidas en `..domain..`** (`@Document`, `@Id`,
  `@Version`, `@Field`): el documento de Mongo es un modelo aparte del agregado. Si un agregado
  necesita una de estas, la frontera ya se rompió.
- Anotaciones de Spring (`@Component`, `@Service`, `@Repository`) prohibidas en `..domain..`.
- **Inyección por campo prohibida** (`@Autowired`, `@Value`, `@Inject`). La inyección es por
  constructor.
- `System.out` y `System.err` prohibidos.
- `java.util.Date`, `Calendar`, `SimpleDateFormat` prohibidos. Se usa `Instant`.
- `*Controller` o `@RestController` solo en `..infrastructure.web..`.
- El tipo de adaptador de Spring Data (`MongoRepository`) no se referencia fuera de
  `..infrastructure..`: el puerto de dominio (`ActivoRepository`) no extiende nada de Spring Data.

Si una regla molesta, se discute el documento y se cambia la regla. No se desactiva.

Dos cosas a tener presentes:

- Las reglas que apuntan a `..application..` llevan `allowEmptyShould(true)` (o
  `withOptionalLayers(true)` en la arquitectura por capas), porque ese paquete todavía no existe.
  Cada una tiene un comentario al lado que dice qué la retira. Sin eso, ArchUnit rechaza la regla
  entera con *"Layer is empty"* antes de evaluar nada.
- **Cada regla se verificó inyectando la violación a propósito** (un `@Component` y un
  `Instant.now()` en una clase temporal de `domain`, compilado, visto fallar, borrado). Es la única
  forma de saber que una regla corre y no solo existe.

## Idioma y nombres

Este repo usa el dominio **íntegramente en español**: tipos (`Activo`, `CodigoActivo`,
`TransicionEstado`), excepciones (`CodigoActivoDuplicado`) y métodos de negocio (`guardar`,
`buscarPorCodigo`, `existePorCodigo`). No solo los métodos, también los tipos — es una divergencia
más amplia que la que hace `atlas-waypoint-backend` con sus nombres de tipo en inglés.

En **inglés** quedan los nombres de colección de Mongo y los campos de los DTO que vean la red: son
contrato, no vocabulario de negocio.

Razón: es un proyecto de portafolio de un autor, no un ERP multiequipo. El costo de coordinación
que justificaría el inglés no existe acá, y el dominio se piensa y se discute en español.

## Commits

Convencionales, validado por `.githooks/commit-msg`:

```
<tipo>(<alcance opcional>): <asunto en minúscula, hasta 80 caracteres>
```

Tipos: `build`, `chore`, `ci`, `docs`, `feat`, `fix`, `perf`, `refactor`, `style`, `test`. El cuerpo
y el pie quedan libres. Los mensajes que genera git solo (`Merge`, `Revert`, `fixup!`, `squash!`) no
se validan.

El `pre-commit` corre `spotlessCheck` y sale en milisegundos si el commit no toca `.java` ni
`.gradle.kts`. **Verifica, nunca arregla**: un hook que reescribe archivos con staging parcial deja
el índice desincronizado y commitea algo que nadie leyó.

## Escapes documentados

Dos, para el día de un upgrade de Boot o de JDK. **CI no los pasa nunca**, así que CI siempre es
estricto.

```bash
./gradlew build -Pcompilacion.fallarPorWarning=false   # apaga -Werror
./gradlew build -Psin-calidad-estricta                 # apaga Error Prone y NullAway
```

Un `-Xlint:-xxx` nuevo en `build.gradle.kts` requiere un comentario que nombre el warning literal y,
si es por un API de tercero, la condición para retirarlo.

## Trampas conocidas

1. **`gradle.properties` no es basura del wrapper.** Los `--add-exports`/`--add-opens` de
   `jdk.compiler` los necesitan google-java-format **y** Error Prone, los dos, porque usan
   internals de `com.sun.tools.javac`. En JDK 17, sin esos flags, los dos fallan con
   `IllegalAccessError`. Si se borra, se rompen las dos piezas a la vez.
2. **Error Prone 2.35+ exige `-XDshould-stop.ifError=FLOW`** como argumento de `compileJava`, o
   rechaza arrancar con *"is not supported by Error Prone"*. No es un flag de `-Xlint`; es parte del
   protocolo de integración `-Xplugin:ErrorProne` con `javac`. Ya está en `build.gradle.kts`; si se
   quita al tocar esa sección, el mensaje de error no menciona ninguno de los dos nombres.
3. **`-Xlint:all,-this-escape,-missing-explicit-ctor` no corre en JDK 17.** Esas dos categorías de
   lint son de JDK 21+; en 17, javac rechaza el flag entero con `invalid flag`. Si el proyecto migra
   a JDK 21, se agregan ahí y se sube el rango del toolchain.
4. **`CodigoActivo` todavía no tiene `toString()`**, porque todavía no tiene cuerpo (Fase 2). Tres
   excepciones que lo concatenan en su mensaje (`ActivoConOrdenAbierta`, `ActivoNoEncontrado`,
   `CodigoActivoDuplicado`) llevan `@SuppressWarnings("ObjectToString")` con un comentario que dice
   cuándo se retira: en el mismo commit que le agregue el campo y el `toString()` a `CodigoActivo`.
5. **El `.gitignore` todavía tiene las reglas `docs/` y `/plan-cmms.md`**, obsoletas desde que la
   documentación de diseño se movió a la raíz del workspace. Se dejan como guarda: si alguien vuelve
   a crear esas rutas dentro de `services/`, no se publican por accidente.

Y una del entorno: los hooks se ejecutan con el `sh` de Git for Windows, así que `./gradlew`
resuelve. Si un hook tuviera CRLF fallaría con `bad interpreter: /bin/sh^M` y **git no avisa**, se lo
saltaría en silencio; de eso se encarga la regla `/.githooks/** text eol=lf` del `.gitattributes`.
