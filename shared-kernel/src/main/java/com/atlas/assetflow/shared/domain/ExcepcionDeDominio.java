package com.atlas.assetflow.shared.domain;

/**
 * Raiz de la jerarquia de excepciones de negocio: una invariante violada, no un fallo tecnico.
 *
 * <p>No es comprobada a proposito, para no obligar a declarar {@code throws} en cada firma del
 * dominio. Cada subclase declara un {@link #codigo()} estable que la capa web traduce sin depender
 * del texto del mensaje.
 */
public abstract class ExcepcionDeDominio extends RuntimeException {

    private final String codigo;

    protected ExcepcionDeDominio(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }

    protected ExcepcionDeDominio(String codigo, String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.codigo = codigo;
    }

    /** Identificador estable del error de negocio, apto para clientes. */
    public String codigo() {
        return codigo;
    }
}
