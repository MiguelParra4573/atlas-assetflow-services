import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone

plugins {
    java
    // Sin version: la fijan los subproyectos cuando aplican estos plugins. Un
    // "apply false" aqui es solo lo que permite usar la sintaxis id(...)
    // en subprojects sin repetir la version en cada build.gradle.kts.
    id("com.diffplug.spotless") version "7.0.3" apply false
    id("net.ltgt.errorprone") version "4.0.1" apply false
}

// Activa los hooks de git automaticamente en cualquier tarea, no solo si se
// lee convenciones.md. No falla el build si el repo no tiene .git todavia
// (por ejemplo, un checkout parcial) ni si git no esta en el PATH.
tasks.register("activarHooks") {
    doLast {
        try {
            exec {
                commandLine("git", "config", "core.hooksPath", ".githooks")
                isIgnoreExitValue = true
            }
        } catch (ignored: Exception) {
            // Sin git en el PATH, no hay nada que configurar: no es un fallo del build.
        }
    }
}
tasks.named("build") { dependsOn("activarHooks") }

allprojects {
    group = "com.atlas.assetflow"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Interruptor de -Werror. En true (default), cualquier warning de javac rompe
// el build. CI nunca pasa este flag en false: el escape es para el dia de un
// upgrade de Boot o de JDK, no para CI.
val fallarPorWarning = (findProperty("compilacion.fallarPorWarning") as String?)?.toBoolean() ?: true

// Desactiva Error Prone y NullAway juntos. Las dos piezas se acoplan a
// internals de javac y son lo que mas se rompe en un upgrade de JDK. CI nunca
// pasa esto.
val calidadEstricta = !hasProperty("sin-calidad-estricta")

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "jacoco")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        // ArchUnit en todos los modulos: un bounded context nuevo hereda las
        // reglas de frontera sin copiar nada. El artefacto es el core, no
        // archunit-junit5: las reglas se escriben como @Test de JUnit 5.
        "testImplementation"("com.tngtech.archunit:archunit:1.4.2")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    // Formato. `./gradlew spotlessApply` formatea; `spotlessCheck` falla el
    // build y lo engancha la tarea `check`. google-java-format en modo AOSP
    // (4 espacios de bloque, 8 de continuacion, 100 columnas) es exactamente
    // el estilo que el codigo ya practicaba a mano. Ver docs/adr/0001.
    configure<com.diffplug.gradle.spotless.SpotlessExtension> {
        java {
            target("src/**/*.java")
            // Sin <version>: la fija el plugin, pineada por su propio build.
            googleJavaFormat().aosp().reflowLongStrings(true).formatJavadoc(true)
            // Unico paso que agrega algo sobre google-java-format: deja las
            // anotaciones TYPE_USE en la misma linea que el tipo, para que
            // `@Id @Nullable UUID id` quede legible el dia que el adaptador
            // Mongo lo necesite.
            formatAnnotations()
            // Sin importOrder ni removeUnusedImports: los hace el propio
            // paso de google-java-format (fixImports), y dos motores sobre
            // el mismo archivo se pelean en cada apply.
        }
        // El markdown no se formatea aqui: lo gobierna el .editorconfig y
        // las tablas de convenciones.md estan hechas a mano.
    }

    // Error Prone y NullAway. Perfil desactivable con -Psin-calidad-estricta
    // para el dia de un upgrade. NullAway se acota a los paquetes marcados
    // con @NullMarked (OnlyNullMarked=true): el alcance lo declara el
    // codigo, no una lista en este build.
    if (calidadEstricta) {
        apply(plugin = "net.ltgt.errorprone")

        dependencies {
            "errorprone"("com.google.errorprone:error_prone_core:2.36.0")
            "errorprone"("com.uber.nullaway:nullaway:0.13.6")
        }

        tasks.withType<JavaCompile>().configureEach {
            // Obligatorio desde Error Prone 2.35: sin este flag, Error Prone rechaza arrancar con
            // "is not supported by Error Prone". No es un flag de -Xlint, es parte del protocolo
            // de integracion -Xplugin:ErrorProne con javac.
            options.compilerArgs.add("-XDshould-stop.ifError=FLOW")
            if (!name.contains("test", ignoreCase = true)) {
                options.errorprone {
                    check("NullAway", CheckSeverity.ERROR)
                    option("NullAway:OnlyNullMarked", "true")
                    option("NullAway:AcknowledgeRestrictiveAnnotations", "true")
                    option("NullAway:CheckOptionalEmptiness", "true")
                }
            } else {
                // Error Prone sigue corriendo en los tests (bugs reales
                // tambien ahi); NullAway no: los tests usan Mockito y
                // fixtures que no estan anotados @NullMarked a proposito.
                options.errorprone {
                    disable("NullAway")
                }
            }
        }
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        // -this-escape y -missing-explicit-ctor son categorias de lint de JDK 21+: no existen en
        // JDK 17 y javac rechaza el flag entero con "invalid flag". Si el proyecto migra a JDK 21
        // (ver docs/adr/0001-harness-de-calidad.md), se agregan aqui.
        options.compilerArgs.addAll(
            listOf(
                "-Xlint:all,-processing,-serial",
            )
        )
        if (fallarPorWarning) {
            options.compilerArgs.add("-Werror")
        }
    }

    // Cobertura. El reporte queda en build/reports/jacoco/test/html/index.html.
    // Sin umbral todavia: ver docs/adr/0001 y convenciones.md § Cobertura.
    tasks.named<Test>("test") {
        finalizedBy(tasks.named("jacocoTestReport"))
    }
    tasks.named<JacocoReport>("jacocoTestReport") {
        dependsOn(tasks.named("test"))
        reports {
            html.required.set(true)
            xml.required.set(true)
        }
    }
}
