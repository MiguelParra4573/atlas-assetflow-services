package com.atlas.assetflow.activos.domain;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Tests de dominio puros (sin Spring). Quitar @Disabled a medida que se implementa cada caso.
 */
@Disabled("TODO Fase 2")
class ActivoTest {

    @Test
    void registrarActivoNuevoQuedaOperativo() {
    }

    @Test
    void darDeBajaActivoSinOtAbiertaCambiaEstadoADadoDeBaja() {
    }

    @Test
    void darDeBajaActivoConOtAbiertaLanzaExcepcionDeDominio() {
    }

    @Test
    void codigoActivoInvalidoSeRechazaAlConstruir() {
    }
}
