package com.atlas.assetflow.shared.domain;

import java.time.Instant;
import java.util.UUID;

/**
 * Hecho de negocio ya ocurrido. Inmutable; se implementa como {@code record}.
 *
 * <p>El {@link #idEvento()} es la clave de idempotencia de los consumidores.
 */
public interface EventoDominio {

    UUID idEvento();

    Instant ocurridoEn();

    default String tipo() {
        return getClass().getSimpleName();
    }
}
