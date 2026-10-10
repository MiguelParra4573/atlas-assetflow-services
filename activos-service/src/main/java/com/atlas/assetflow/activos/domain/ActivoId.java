package com.atlas.assetflow.activos.domain;

import java.util.Objects;
import java.util.UUID;

/**
 * Identidad tecnica del agregado {@link Activo}, separada del {@link CodigoActivo}: el codigo es un
 * dato de negocio corregible, esta identidad es inmutable y es la que referencian otros contextos.
 *
 * <p>Se genera en el dominio, no en la base de datos: el agregado es valido antes de persistirse.
 */
public record ActivoId(UUID valor) {

    public ActivoId {
        Objects.requireNonNull(valor, "ActivoId no puede ser null");
    }

    public static ActivoId nuevo() {
        return new ActivoId(UUID.randomUUID());
    }

    public static ActivoId de(String valor) {
        Objects.requireNonNull(valor, "ActivoId no puede ser null");
        try {
            return new ActivoId(UUID.fromString(valor));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ActivoId no es un UUID valido: " + valor, e);
        }
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
