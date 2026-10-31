package com.atlas.assetflow.activos.domain;

import com.atlas.assetflow.shared.domain.ExcepcionDeDominio;

/** El texto recibido no cumple {@link CodigoActivo#PATRON}. La lanza su constructor. */
public class CodigoActivoInvalido extends ExcepcionDeDominio {

    public static final String CODIGO = "CODIGO_ACTIVO_INVALIDO";

    public CodigoActivoInvalido(String valorRecibido) {
        super(
                CODIGO,
                "El codigo de activo no cumple el formato "
                        + CodigoActivo.PATRON.pattern()
                        + ": "
                        + valorRecibido);
    }
}
