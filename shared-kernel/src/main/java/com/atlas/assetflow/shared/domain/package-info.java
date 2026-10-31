/**
 * Shared kernel: lo minimo que todos los bounded contexts comparten.
 *
 * <p>Regla de admision: aqui solo entra lo que los contextos comparten de verdad y que no cambia
 * por razones de negocio de ninguno de ellos. Nada de agregados, value objects de negocio ni enums
 * de dominio. Sin dependencias de Spring ni de la base de datos.
 */
@org.jspecify.annotations.NullMarked
package com.atlas.assetflow.shared.domain;
