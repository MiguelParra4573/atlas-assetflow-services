package com.atlas.assetflow.activos;

import static com.atlas.assetflow.activos.ClasesDelProyecto.CLASES;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

/**
 * Buenas practicas dentro de una clase, verificadas igual que la arquitectura: inyectando la
 * violacion a proposito es la unica forma de saber que una regla corre. Documentadas en {@code
 * services/convenciones.md} § Las reglas verificadas.
 */
class ConvencionesDeCodigoTests {

    @Test
    void elDominioNoLeeElRelojDelSistema() {
        // El instante entra por el puerto Reloj (shared-kernel), nunca por Instant.now(): es lo
        // que hace deterministas los tests de duraciones. docs/arquitectura.md § 5 lo declara;
        // antes de este test era solo una convencion escrita, sin nada que la verificara.
        ArchRule regla =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .callMethod(java.time.Instant.class, "now")
                        .orShould()
                        .callMethod(java.time.LocalDate.class, "now")
                        .orShould()
                        .callMethod(System.class, "currentTimeMillis");
        regla.check(CLASES);
    }

    @Test
    void elDominioNoLlevaAnotacionesDePersistencia() {
        // El documento de Mongo es un modelo aparte del agregado (services/README.md § Dominio sin
        // infraestructura). Si un agregado necesita una de estas anotaciones, la frontera ya se
        // rompio: no es un adaptador el que fallo, es el agregado.
        ArchRule regla =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .beAnnotatedWith("org.springframework.data.mongodb.core.mapping.Document")
                        .orShould()
                        .beAnnotatedWith("org.springframework.data.annotation.Id")
                        .orShould()
                        .beAnnotatedWith("org.springframework.data.annotation.Version")
                        .orShould()
                        .beAnnotatedWith("org.springframework.data.mongodb.core.mapping.Field");
        regla.check(CLASES);
    }

    @Test
    void elDominioNoLlevaAnotacionesDeSpring() {
        ArchRule regla =
                noClasses()
                        .that()
                        .resideInAPackage("..domain..")
                        .should()
                        .beAnnotatedWith("org.springframework.stereotype.Component")
                        .orShould()
                        .beAnnotatedWith("org.springframework.stereotype.Service")
                        .orShould()
                        .beAnnotatedWith("org.springframework.stereotype.Repository");
        regla.check(CLASES);
    }

    @Test
    void laInyeccionEsPorConstructorNoPorCampo() {
        // Por constructor: la dependencia queda visible en la firma y la clase es testeable sin
        // contenedor. La inyeccion por campo oculta las dos cosas. Se expresa sobre fields(), no
        // sobre classes(): no hay una clase a senalar, hay un campo.
        ArchRule regla =
                noFields()
                        .that()
                        .areDeclaredInClassesThat()
                        .resideInAPackage("com.atlas.assetflow..")
                        .should()
                        .beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired")
                        .orShould()
                        .beAnnotatedWith("org.springframework.beans.factory.annotation.Value")
                        .orShould()
                        .beAnnotatedWith("jakarta.inject.Inject");
        regla.check(CLASES);
    }

    @Test
    void nadaEscribeDirectoAConsola() {
        // System.out / System.err son campos, no metodos: se verifican como acceso a campo.
        ArchRule regla =
                noClasses()
                        .should()
                        .accessField(System.class, "out")
                        .orShould()
                        .accessField(System.class, "err");
        regla.check(CLASES);
    }

    @Test
    void nadaUsaLasClasesDeFechaViejas() {
        // Se usa Instant. java.util.Date y Calendar son mutables y sin zona explicita.
        ArchRule regla =
                noClasses()
                        .should()
                        .dependOnClassesThat()
                        .haveFullyQualifiedName(java.util.Date.class.getName())
                        .orShould()
                        .dependOnClassesThat()
                        .haveFullyQualifiedName(java.util.Calendar.class.getName())
                        .orShould()
                        .dependOnClassesThat()
                        .haveFullyQualifiedName(java.text.SimpleDateFormat.class.getName());
        regla.check(CLASES);
    }

    @Test
    void losControladoresVivenEnInfrastructureWeb() {
        // allowEmptyShould(true): hoy no hay ningun @RestController, solo el
        // @RestControllerAdvice de ManejadorDeErrores. Se retira cuando exista el primero.
        ArchRule regla =
                classes()
                        .that()
                        .haveSimpleNameEndingWith("Controller")
                        .or()
                        .areAnnotatedWith("org.springframework.web.bind.annotation.RestController")
                        .should()
                        .resideInAPackage("..infrastructure.web..")
                        .allowEmptyShould(true);
        regla.check(CLASES);
    }

    @Test
    void losRepositoriosDeSpringDataSonAdaptadoresNoPuertos() {
        // El puerto (la interfaz de dominio) se llama *Repository y vive en domain, sin extender
        // nada de Spring Data; su implementacion real lo hace y vive en infrastructure. Esta regla
        // prohibe que el tipo de adaptador de Spring Data aparezca referenciado fuera de
        // infrastructure.
        ArchRule regla =
                noClasses()
                        .that()
                        .resideOutsideOfPackage("..infrastructure..")
                        .should()
                        .dependOnClassesThat()
                        .haveFullyQualifiedName(
                                "org.springframework.data.mongodb.repository.MongoRepository");
        regla.check(CLASES);
    }
}
