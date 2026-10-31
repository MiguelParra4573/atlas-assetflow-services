/**
 * Dominio del contexto Activos: el agregado {@link Activo}, sus value objects, sus eventos y el
 * puerto de repositorio. Sin dependencias de Spring, de Mongo ni de la capa web — ver {@code
 * infrastructure/package-info.java}, que declara la prohibicion inversa.
 *
 * <p>Marcado {@code @NullMarked}: dentro de este paquete todo es no-nulo salvo lo que se marque
 * explicitamente con {@code @Nullable}. Ver {@code docs/adr/0001-harness-de-calidad.md}.
 */
@org.jspecify.annotations.NullMarked
package com.atlas.assetflow.activos.domain;
