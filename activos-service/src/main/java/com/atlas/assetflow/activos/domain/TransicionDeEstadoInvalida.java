package com.atlas.assetflow.activos.domain;

import com.atlas.assetflow.shared.domain.ExcepcionDeDominio;

/**
 * Transicion no permitida por el ciclo de vida del activo. Repetir una transicion hacia un estado
 * terminal es error, no operacion idempotente.
 */
public class TransicionDeEstadoInvalida extends ExcepcionDeDominio {

    public static final String CODIGO = "TRANSICION_DE_ESTADO_INVALIDA";

    public TransicionDeEstadoInvalida(EstadoActivo desde, EstadoActivo hacia) {
        super(CODIGO, "Transicion de estado no permitida: " + desde + " -> " + hacia);
    }
}
