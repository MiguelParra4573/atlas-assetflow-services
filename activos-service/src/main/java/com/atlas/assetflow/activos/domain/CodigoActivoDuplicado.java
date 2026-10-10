package com.atlas.assetflow.activos.domain;

import com.atlas.assetflow.shared.domain.ExcepcionDeDominio;

/**
 * Ya existe un activo con ese codigo.
 *
 * <p>La lanza el servicio de aplicacion, no el agregado: la unicidad es una invariante del conjunto
 * y comprobarla exige consultar el repositorio.
 */
public class CodigoActivoDuplicado extends ExcepcionDeDominio {

    public static final String CODIGO = "CODIGO_ACTIVO_DUPLICADO";

    public CodigoActivoDuplicado(CodigoActivo codigo) {
        super(CODIGO, "Ya existe un activo con el codigo " + codigo);
    }
}
