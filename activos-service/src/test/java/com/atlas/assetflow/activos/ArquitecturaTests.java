package com.atlas.assetflow.activos;

import static com.atlas.assetflow.activos.ClasesDelProyecto.CLASES;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import org.junit.jupiter.api.Test;

/**
 * Las fronteras hexagonales declaradas en {@code docs/arquitectura.md} § 2.3, hechas ejecutables.
 *
 * <p>Antes de este archivo, la regla de dependencia vivia solo como javadoc en {@code
 * activos/infrastructure/package-info.java}: "Nada de aqui puede ser importado desde domain". Un
 * import nuevo no rompia nada. Ahora rompe el build.
 *
 * <p>Nota de ArchUnit: una {@code ArchCondition} usada con {@code noClasses().should(...)} tiene
 * que registrar el hallazgo con {@code SimpleConditionEvent.satisfied}, nunca con {@code violated}
 * — {@code noClasses()} envuelve la condicion en una negacion que invierte la bandera de cada
 * evento, asi que con {@code violated} la regla pasaria sobre el codigo que la viola, sin dar
 * ninguna senal. Las reglas de abajo usan solo predicados de ArchUnit ya probados ({@code
 * dependOnClassesThat}, {@code resideInAPackage}), nunca una condicion propia, justo para no caer
 * en esa trampa.
 */
class ArquitecturaTests {

    @Test
    void dominioNoDependeDeInfraestructura() {
        ArchRule regla =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("..infrastructure..", "..application..");
        regla.check(CLASES);
    }

    @Test
    void dominioNoDependeDeSpringNiDeMongo() {
        ArchRule regla =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("org.springframework..", "org.bson..", "com.mongodb..");
        regla.check(CLASES);
    }

    @Test
    void aplicacionNoDependeDeInfraestructura() {
        // allowEmptyShould(true): el paquete application todavia no existe (ver convenciones.md §
        // Las reglas verificadas). Se retira cuando application/ tenga al menos una clase.
        ArchRule regla =
                noClasses()
                        .that()
                        .resideInAPackage("..application..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAPackage("..infrastructure..")
                        .allowEmptyShould(true);
        regla.check(CLASES);
    }

    @Test
    void sharedKernelNoDependeDeNadaDeInfraestructura() {
        // El propio build.gradle.kts de shared-kernel ya lo garantiza (sin Spring ni Mongo en sus
        // dependencias): esta regla lo hace visible y lo mantiene asi aunque alguien edite ese
        // build.gradle.kts sin leer el comentario.
        ArchRule regla =
                noClasses()
                        .that()
                        .resideInAPackage("com.atlas.assetflow.shared..")
                        .should()
                        .dependOnClassesThat()
                        .resideInAnyPackage("org.springframework..", "org.bson..", "com.mongodb..");
        regla.check(CLASES);
    }

    @Test
    void capasRespetanElOrdenDelHexagono() {
        // consideringOnlyDependenciesInLayers(): sin ella, la regla tambien evaluaria las
        // dependencias de cada capa hacia fuera del proyecto (Spring, el JDK) y "Infraestructura"
        // dejaria de ser accesible por nadie seria trivialmente cierto por razones equivocadas.
        ArchRule regla =
                Architectures.layeredArchitecture()
                        .consideringOnlyDependenciesInLayers()
                        // La capa Aplicacion todavia no tiene ninguna clase (ver convenciones.md
                        // § Las reglas verificadas): sin esto, ArchUnit rechaza la regla entera
                        // por "Layer is empty" antes de evaluar ninguna dependencia. Se retira
                        // cuando application/ tenga su primera clase.
                        .withOptionalLayers(true)
                        .layer("Dominio")
                        .definedBy("..domain..")
                        .layer("Aplicacion")
                        .definedBy("..application..")
                        .layer("Infraestructura")
                        .definedBy("..infrastructure..")
                        .whereLayer("Infraestructura")
                        .mayNotBeAccessedByAnyLayer()
                        .whereLayer("Aplicacion")
                        .mayOnlyBeAccessedByLayers("Infraestructura");
        regla.check(CLASES);
    }

    @Test
    void aplicacionSoloEsAccesibleDesdeInfraestructura() {
        // Misma validez de allowEmptyShould que arriba: application/ no existe todavia.
        ArchRule regla =
                classes()
                        .that()
                        .resideInAPackage("..application..")
                        .should()
                        .onlyBeAccessed()
                        .byAnyPackage("..infrastructure..", "..application..")
                        .allowEmptyShould(true);
        regla.check(CLASES);
    }
}
