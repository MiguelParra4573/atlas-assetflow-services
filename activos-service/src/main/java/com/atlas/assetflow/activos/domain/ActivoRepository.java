package com.atlas.assetflow.activos.domain;

import com.atlas.assetflow.shared.domain.Pagina;
import com.atlas.assetflow.shared.domain.SolicitudPagina;
import java.util.Optional;

/**
 * Puerto de dominio del agregado {@link Activo}. Sin dependencias de Spring ni Mongo: la
 * implementacion vive en {@code infrastructure}.
 *
 * <p>{@link #existePorCodigo} permite al servicio de aplicacion comprobar la unicidad del codigo
 * sin cargar el agregado completo: esa invariante es del conjunto, no del agregado.
 */
public interface ActivoRepository {

    Activo guardar(Activo activo);

    Optional<Activo> buscarPorId(ActivoId id);

    Optional<Activo> buscarPorCodigo(CodigoActivo codigo);

    boolean existePorCodigo(CodigoActivo codigo);

    Pagina<Activo> listar(SolicitudPagina solicitud);
}
