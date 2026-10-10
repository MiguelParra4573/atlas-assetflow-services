package com.atlas.assetflow.activos.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Un cambio de estado del activo, con su instante. Se guarda el historial y no solo el estado
 * actual porque los indicadores de disponibilidad necesitan el tiempo acumulado en cada estado.
 *
 * @param estadoAnterior estado del que se sale; {@code null} en el registro inicial
 * @param estadoNuevo    estado al que se entra
 * @param ocurridaEn     instante del cambio
 * @param motivo         la orden de trabajo que la provoco, o la razon de la decision manual
 */
public record TransicionEstado(
        EstadoActivo estadoAnterior,
        EstadoActivo estadoNuevo,
        Instant ocurridaEn,
        String motivo) {

    public TransicionEstado {
        Objects.requireNonNull(estadoNuevo, "estadoNuevo no puede ser null");
        Objects.requireNonNull(ocurridaEn, "ocurridaEn no puede ser null");
    }

    /** Transicion inicial, la del registro del activo. */
    public static TransicionEstado inicial(EstadoActivo estadoNuevo, Instant ocurridaEn) {
        return new TransicionEstado(null, estadoNuevo, ocurridaEn, "registro del activo");
    }

    public boolean esInicial() {
        return estadoAnterior == null;
    }
}
