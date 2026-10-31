package com.atlas.assetflow.activos.domain;

import com.atlas.assetflow.shared.domain.ExcepcionDeDominio;

/**
 * No existe el activo solicitado. La lanza el servicio de aplicacion tras consultar el repositorio.
 */
public class ActivoNoEncontrado extends ExcepcionDeDominio {

    public static final String CODIGO = "ACTIVO_NO_ENCONTRADO";

    public ActivoNoEncontrado(ActivoId id) {
        super(CODIGO, "No existe el activo " + id);
    }

    // ObjectToString: ver la nota en ActivoConOrdenAbierta — CodigoActivo todavia no tiene
    // toString() propio (Fase 2, TODO en CodigoActivo.java).
    @SuppressWarnings("ObjectToString")
    public ActivoNoEncontrado(CodigoActivo codigo) {
        super(CODIGO, "No existe el activo con codigo " + codigo);
    }
}
