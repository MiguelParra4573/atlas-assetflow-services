package com.atlas.assetflow.activos.domain.evento;

import com.atlas.assetflow.activos.domain.ActivoId;
import com.atlas.assetflow.activos.domain.CodigoActivo;
import com.atlas.assetflow.shared.domain.EventoDominio;

import java.time.Instant;
import java.util.UUID;

/**
 * Un activo salio de operacion definitivamente. Lo consume Mantenimiento para bloquear ordenes
 * futuras sobre ese activo y pausar sus planes preventivos.
 */
public record ActivoDadoDeBaja(
        UUID idEvento,
        Instant ocurridoEn,
        ActivoId activoId,
        CodigoActivo codigo,
        String motivo) implements EventoDominio {

    public static ActivoDadoDeBaja de(
            ActivoId activoId, CodigoActivo codigo, String motivo, Instant ocurridoEn) {
        return new ActivoDadoDeBaja(UUID.randomUUID(), ocurridoEn, activoId, codigo, motivo);
    }
}
