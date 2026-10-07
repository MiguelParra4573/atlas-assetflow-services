package com.atlas.assetflow.activos.domain;

import java.util.Optional;

/**
 * Puerto de dominio del agregado {@link Activo}. Sin dependencias de Spring ni Mongo:
 * la implementación vive en {@code infrastructure}.
 */
public interface ActivoRepository {

    Activo guardar(Activo activo);

    Optional<Activo> buscarPorCodigo(CodigoActivo codigo);

    boolean existePorCodigo(CodigoActivo codigo);
}
