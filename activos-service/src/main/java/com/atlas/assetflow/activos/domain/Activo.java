package com.atlas.assetflow.activos.domain;

import com.atlas.assetflow.shared.domain.RaizAgregado;

/**
 * Agregado raiz del contexto Activos.
 *
 * <p>Invariante: no se puede dar de baja un activo que tiene una orden de trabajo abierta. La regla
 * vive dentro del agregado, que recibe ese dato como parametro del comando.
 *
 * <p>TODO (Fase 2): modelar registro, cambio de estado y baja. La identidad es {@link ActivoId};
 * {@link CodigoActivo} es un atributo unico de negocio. Acumula sus eventos con
 * {@code registrarEvento} y guarda el historial de {@link TransicionEstado}. Sin {@code setEstado}
 * publico: las transiciones permitidas las decide el agregado.
 */
public class Activo extends RaizAgregado {
}
