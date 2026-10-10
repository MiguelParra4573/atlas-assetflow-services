package com.atlas.assetflow.activos.domain;

import com.atlas.assetflow.shared.domain.ExcepcionDeDominio;

/**
 * Intento de dar de baja un activo que tiene una orden de trabajo abierta.
 *
 * <p>Es la invariante central del contexto. Vive dentro de {@code Activo.darDeBaja(...)}, que
 * recibe el dato de ordenes abiertas como parametro del comando: el agregado no consulta a otro
 * contexto por su cuenta.
 */
public class ActivoConOrdenAbierta extends ExcepcionDeDominio {

    public static final String CODIGO = "ACTIVO_CON_ORDEN_ABIERTA";

    public ActivoConOrdenAbierta(CodigoActivo codigo) {
        super(CODIGO, "El activo " + codigo
                + " no se puede dar de baja porque tiene una orden de trabajo abierta");
    }
}
