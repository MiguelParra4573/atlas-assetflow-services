package com.atlas.assetflow.activos.domain;

/**
 * Agregado raíz del contexto Activos.
 *
 * Invariante (docs/diseno-dominio.md, sección 3): no se puede dar de baja un activo
 * que tiene una OT abierta (PENDIENTE o EN_PROGRESO). La regla vive dentro del agregado.
 *
 * TODO (Fase 2): implementar identidad, estado, registro y baja con sus reglas.
 */
public class Activo {
}
