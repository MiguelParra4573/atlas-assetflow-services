package com.atlas.assetflow.activos.domain.evento;

import com.atlas.assetflow.activos.domain.ActivoId;
import com.atlas.assetflow.activos.domain.CodigoActivo;
import com.atlas.assetflow.shared.domain.EventoDominio;

import java.time.Instant;
import java.util.UUID;

/** Se dio de alta un activo nuevo con codigo unico. */
public record ActivoRegistrado(
        UUID idEvento,
        Instant ocurridoEn,
        ActivoId activoId,
        CodigoActivo codigo) implements EventoDominio {

    public static ActivoRegistrado de(ActivoId activoId, CodigoActivo codigo, Instant ocurridoEn) {
        return new ActivoRegistrado(UUID.randomUUID(), ocurridoEn, activoId, codigo);
    }
}
