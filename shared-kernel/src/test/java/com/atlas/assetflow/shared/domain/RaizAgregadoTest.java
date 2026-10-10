package com.atlas.assetflow.shared.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RaizAgregadoTest {

    private record EventoDePrueba(UUID idEvento, Instant ocurridoEn) implements EventoDominio {
    }

    private static final class AgregadoDePrueba extends RaizAgregado {
        void hacerAlgo(Instant cuando) {
            registrarEvento(new EventoDePrueba(UUID.randomUUID(), cuando));
        }

        void registrarNulo() {
            registrarEvento(null);
        }
    }

    @Test
    void pullEventosDevuelveLoAcumuladoYDejaElAgregadoLimpio() {
        AgregadoDePrueba agregado = new AgregadoDePrueba();
        Instant ahora = Instant.parse("2026-10-09T12:00:00Z");

        agregado.hacerAlgo(ahora);
        agregado.hacerAlgo(ahora);

        List<EventoDominio> primera = agregado.pullEventos();
        assertEquals(2, primera.size());
        assertTrue(agregado.pullEventos().isEmpty(), "el segundo pull debe venir vacio");
    }

    @Test
    void eventosPendientesConsultaSinVaciar() {
        AgregadoDePrueba agregado = new AgregadoDePrueba();
        agregado.hacerAlgo(Instant.EPOCH);

        assertEquals(1, agregado.eventosPendientes().size());
        assertEquals(1, agregado.eventosPendientes().size());
        assertEquals(1, agregado.pullEventos().size());
    }

    @Test
    void laListaDevueltaEsInmutable() {
        AgregadoDePrueba agregado = new AgregadoDePrueba();
        agregado.hacerAlgo(Instant.EPOCH);

        List<EventoDominio> eventos = agregado.pullEventos();

        assertThrows(UnsupportedOperationException.class,
                () -> eventos.add(new EventoDePrueba(UUID.randomUUID(), Instant.EPOCH)));
    }

    @Test
    void rechazaEventoNulo() {
        AgregadoDePrueba agregado = new AgregadoDePrueba();

        assertThrows(NullPointerException.class, () -> agregado.registrarNulo());
    }

    @Test
    void cadaEventoLlevaIdentidadPropiaParaDeduplicar() {
        AgregadoDePrueba agregado = new AgregadoDePrueba();
        agregado.hacerAlgo(Instant.EPOCH);
        agregado.hacerAlgo(Instant.EPOCH);

        List<EventoDominio> eventos = agregado.pullEventos();

        assertNotEquals(eventos.get(0).idEvento(), eventos.get(1).idEvento());
    }

    @Test
    void elTipoPorOmisionEsElNombreDeLaClase() {
        EventoDominio evento = new EventoDePrueba(UUID.randomUUID(), Instant.EPOCH);

        assertEquals("EventoDePrueba", evento.tipo());
    }

    @Test
    void relojFijoEsDeterminista() {
        Instant instante = Instant.parse("2026-10-09T12:00:00Z");
        Reloj reloj = Reloj.fijo(instante);

        assertEquals(instante, reloj.ahora());
        assertEquals(instante, reloj.ahora());
    }

    @Test
    void relojDelSistemaAvanza() {
        Instant antes = Instant.parse("2000-01-01T00:00:00Z");

        assertTrue(Reloj.delSistema().ahora().isAfter(antes));
    }
}
