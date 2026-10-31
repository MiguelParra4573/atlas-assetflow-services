package com.atlas.assetflow.activos.domain;

/**
 * Estado del ciclo de vida de un {@link Activo}: OPERATIVO, EN_MANTENIMIENTO, DADO_DE_BAJA.
 *
 * <p>Transiciones permitidas: entre OPERATIVO y EN_MANTENIMIENTO en ambos sentidos, y hacia
 * DADO_DE_BAJA solo por la via de la baja del activo, que valida la invariante de orden abierta.
 * DADO_DE_BAJA es terminal: cualquier otra transicion lanza {@link TransicionDeEstadoInvalida}.
 *
 * <p>TODO (Fase 2): definir los valores y las transiciones permitidas.
 */
public enum EstadoActivo {}
