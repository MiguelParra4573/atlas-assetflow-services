package com.atlas.assetflow.activos;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;

/**
 * Punto unico de importacion para {@link ArquitecturaTests} y {@link ConvencionesDeCodigoTests}.
 *
 * <p>{@code DO_NOT_INCLUDE_TESTS} es el invariante que hace seguras las dos baterias de reglas: los
 * tests usan a proposito cosas que las reglas prohiben (por ejemplo, el repositorio de Spring Data
 * se mockea fuera de {@code infrastructure}), y eso es legitimo. Si alguna vez se analizan los
 * tests, las reglas empiezan a fallar sobre codigo que no viola nada.
 */
final class ClasesDelProyecto {

    static final com.tngtech.archunit.core.domain.JavaClasses CLASES =
            new ClassFileImporter()
                    .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                    .importPackages("com.atlas.assetflow");

    private ClasesDelProyecto() {}
}
