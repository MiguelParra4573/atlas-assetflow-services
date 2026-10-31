package com.atlas.assetflow.shared.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class PaginaTest {

    @Test
    void totalPaginasRedondeaHaciaArriba() {
        Pagina<String> pagina = new Pagina<>(List.of("a", "b"), 0, 3, 7);

        assertEquals(3, pagina.totalPaginas());
    }

    @Test
    void sinElementosNoHayPaginas() {
        Pagina<String> pagina = new Pagina<>(List.of(), 0, 20, 0);

        assertEquals(0, pagina.totalPaginas());
        assertFalse(pagina.hayPaginaSiguiente());
    }

    @Test
    void hayPaginaSiguienteMientrasQuedenElementos() {
        assertTrue(new Pagina<>(List.of("a"), 0, 1, 2).hayPaginaSiguiente());
        assertFalse(new Pagina<>(List.of("b"), 1, 1, 2).hayPaginaSiguiente());
    }

    @Test
    void elContenidoQuedaInmutable() {
        List<String> original = new ArrayList<>(List.of("a"));
        Pagina<String> pagina = new Pagina<>(original, 0, 20, 1);

        original.add("b");

        assertEquals(1, pagina.contenido().size());
        assertThrows(UnsupportedOperationException.class, () -> pagina.contenido().add("c"));
    }

    @Test
    void rechazaParametrosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> new Pagina<>(null, 0, 20, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pagina<>(List.of(), -1, 20, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pagina<>(List.of(), 0, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> new Pagina<>(List.of(), 0, 20, -1));
    }

    @Test
    void solicitudRecortaEnLugarDeRechazar() {
        assertEquals(SolicitudPagina.TAMANO_MAXIMO, new SolicitudPagina(0, 10_000).tamano());
        assertEquals(SolicitudPagina.TAMANO_POR_OMISION, new SolicitudPagina(0, 0).tamano());
        assertEquals(0, new SolicitudPagina(-5, 20).pagina());
    }

    @Test
    void desplazamientoNoDesbordaConPaginasAltas() {
        SolicitudPagina solicitud = new SolicitudPagina(Integer.MAX_VALUE, 200);

        assertEquals((long) Integer.MAX_VALUE * 200, solicitud.desplazamiento());
    }

    @Test
    void laFabricaPropagaLosParametrosDeLaSolicitud() {
        Pagina<String> pagina = Pagina.de(List.of("a"), new SolicitudPagina(2, 5), 11);

        assertEquals(2, pagina.pagina());
        assertEquals(5, pagina.tamano());
        assertEquals(3, pagina.totalPaginas());
    }
}
