package com.atlas.assetflow.shared.domain;

/**
 * Parametros de paginacion de entrada: {@code ?pagina=0&tamano=20}, ambos opcionales.
 *
 * <p>El tamano se recorta a {@link #TAMANO_MAXIMO} en lugar de rechazarse: un listado sin tope
 * permite tumbar el servicio con una sola peticion.
 *
 * @param pagina indice base cero
 * @param tamano elementos por pagina
 */
public record SolicitudPagina(int pagina, int tamano) {

    public static final int TAMANO_POR_OMISION = 20;
    public static final int TAMANO_MAXIMO = 200;

    public SolicitudPagina {
        if (pagina < 0) {
            pagina = 0;
        }
        if (tamano <= 0) {
            tamano = TAMANO_POR_OMISION;
        }
        if (tamano > TAMANO_MAXIMO) {
            tamano = TAMANO_MAXIMO;
        }
    }

    public static SolicitudPagina porOmision() {
        return new SolicitudPagina(0, TAMANO_POR_OMISION);
    }

    /** Elementos a saltar. Lo usa el adaptador de persistencia. */
    public long desplazamiento() {
        return (long) pagina * tamano;
    }
}
