package com.atlas.assetflow.shared.domain;

/**
 * Dos escrituras concurrentes sobre el mismo agregado.
 *
 * <p>El adaptador de persistencia traduce aqui el fallo de bloqueo optimista, para que la capa web
 * no dependa de tipos de infraestructura. Es reintentable: la operacion puede repetirse.
 */
public class ConflictoDeConcurrencia extends ExcepcionDeDominio {

    public static final String CODIGO = "CONFLICTO_DE_CONCURRENCIA";

    public ConflictoDeConcurrencia(String agregado, Object identidad, Throwable causa) {
        super(CODIGO,
                "Otra operacion modifico " + agregado + " " + identidad
                        + " al mismo tiempo. Reintenta la operacion.",
                causa);
    }
}
