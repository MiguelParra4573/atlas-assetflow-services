package com.atlas.assetflow.activos.domain.evento;

import com.atlas.assetflow.activos.domain.ActivoId;
import com.atlas.assetflow.activos.domain.EstadoActivo;
import com.atlas.assetflow.shared.domain.EventoDominio;

import java.time.Instant;
import java.util.UUID;

/**
 * Un activo cambio de estado. Lleva estado anterior y nuevo porque los consumidores necesitan el
 * delta: los tiempos de parada se calculan a partir de las transiciones.
 */
public record ActivoCambioEstado(
        UUID idEvento,
        Instant ocurridoEn,
        ActivoId activoId,
        EstadoActivo estadoAnterior,
        EstadoActivo estadoNuevo,
        String motivo) implements EventoDominio {

    public static ActivoCambioEstado de(
            ActivoId activoId,
            EstadoActivo estadoAnterior,
            EstadoActivo estadoNuevo,
            String motivo,
            Instant ocurridoEn) {
        return new ActivoCambioEstado(
                UUID.randomUUID(), ocurridoEn, activoId, estadoAnterior, estadoNuevo, motivo);
    }
}
