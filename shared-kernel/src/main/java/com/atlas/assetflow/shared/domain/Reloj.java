package com.atlas.assetflow.shared.domain;

import java.time.Instant;

/**
 * Puerto de tiempo del dominio. Ningun agregado llama a {@code Instant.now()} directamente: con
 * este puerto los tests de duraciones son deterministas.
 */
public interface Reloj {

    Instant ahora();

    /** Reloj real. Es el que se registra como bean en produccion. */
    static Reloj delSistema() {
        return Instant::now;
    }

    /** Reloj detenido, para tests deterministas. */
    static Reloj fijo(Instant instante) {
        return () -> instante;
    }
}
