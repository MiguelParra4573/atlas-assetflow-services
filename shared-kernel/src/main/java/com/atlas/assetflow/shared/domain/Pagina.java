package com.atlas.assetflow.shared.domain;

import java.util.List;

/**
 * Pagina de resultados. Forma unica de respuesta de todo listado del sistema.
 *
 * @param contenido       elementos de esta pagina
 * @param pagina          indice de pagina solicitado, base cero
 * @param tamano          tamano de pagina solicitado
 * @param totalElementos  total de elementos que cumplen el filtro, en todas las paginas
 */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos) {

    public Pagina {
        if (contenido == null) {
            throw new IllegalArgumentException("contenido no puede ser null");
        }
        if (pagina < 0) {
            throw new IllegalArgumentException("pagina no puede ser negativa: " + pagina);
        }
        if (tamano <= 0) {
            throw new IllegalArgumentException("tamano debe ser positivo: " + tamano);
        }
        if (totalElementos < 0) {
            throw new IllegalArgumentException("totalElementos no puede ser negativo: " + totalElementos);
        }
        contenido = List.copyOf(contenido);
    }

    public int totalPaginas() {
        return (int) Math.ceil((double) totalElementos / tamano);
    }

    public boolean hayPaginaSiguiente() {
        return (long) (pagina + 1) * tamano < totalElementos;
    }

    public static <T> Pagina<T> de(List<T> contenido, SolicitudPagina solicitud, long totalElementos) {
        return new Pagina<>(contenido, solicitud.pagina(), solicitud.tamano(), totalElementos);
    }
}
