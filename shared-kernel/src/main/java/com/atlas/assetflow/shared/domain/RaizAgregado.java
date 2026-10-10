package com.atlas.assetflow.shared.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base de las raices de agregado que acumulan sus eventos de dominio.
 *
 * <p>El agregado registra el evento despues de aplicar el cambio y validar sus invariantes. La capa
 * de aplicacion persiste y luego vacia los eventos con {@link #pullEventos()} para publicarlos.
 */
public abstract class RaizAgregado {

    private final List<EventoDominio> eventos = new ArrayList<>();

    protected void registrarEvento(EventoDominio evento) {
        eventos.add(Objects.requireNonNull(evento, "el evento de dominio no puede ser null"));
    }

    /** Devuelve los eventos acumulados y deja el agregado limpio. */
    public List<EventoDominio> pullEventos() {
        List<EventoDominio> copia = List.copyOf(eventos);
        eventos.clear();
        return copia;
    }

    /** Consulta sin vaciar. Util en tests de dominio. */
    public List<EventoDominio> eventosPendientes() {
        return List.copyOf(eventos);
    }
}
