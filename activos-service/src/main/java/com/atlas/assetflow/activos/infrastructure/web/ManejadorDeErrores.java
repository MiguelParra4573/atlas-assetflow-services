package com.atlas.assetflow.activos.infrastructure.web;

import com.atlas.assetflow.activos.domain.ActivoConOrdenAbierta;
import com.atlas.assetflow.activos.domain.ActivoNoEncontrado;
import com.atlas.assetflow.activos.domain.CodigoActivoDuplicado;
import com.atlas.assetflow.activos.domain.CodigoActivoInvalido;
import com.atlas.assetflow.activos.domain.TransicionDeEstadoInvalida;
import com.atlas.assetflow.shared.domain.ConflictoDeConcurrencia;
import com.atlas.assetflow.shared.domain.ExcepcionDeDominio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Traduce las excepciones de dominio a respuestas HTTP con {@link ProblemDetail} (RFC 7807).
 *
 * <p>El cuerpo lleva siempre un campo {@code codigo} estable, para que el cliente decida sin
 * parsear mensajes. Estados: 400 dato de entrada invalido, 404 agregado inexistente, 409 invariante
 * de negocio violada o conflicto de concurrencia.
 */
@RestControllerAdvice
public class ManejadorDeErrores {

    @ExceptionHandler(CodigoActivoInvalido.class)
    public ProblemDetail codigoInvalido(CodigoActivoInvalido e) {
        return problema(HttpStatus.BAD_REQUEST, "Codigo de activo invalido", e);
    }

    @ExceptionHandler(ActivoNoEncontrado.class)
    public ProblemDetail noEncontrado(ActivoNoEncontrado e) {
        return problema(HttpStatus.NOT_FOUND, "Activo no encontrado", e);
    }

    @ExceptionHandler(CodigoActivoDuplicado.class)
    public ProblemDetail duplicado(CodigoActivoDuplicado e) {
        return problema(HttpStatus.CONFLICT, "Codigo de activo duplicado", e);
    }

    @ExceptionHandler(ActivoConOrdenAbierta.class)
    public ProblemDetail conOrdenAbierta(ActivoConOrdenAbierta e) {
        return problema(HttpStatus.CONFLICT, "El activo tiene una orden de trabajo abierta", e);
    }

    @ExceptionHandler(TransicionDeEstadoInvalida.class)
    public ProblemDetail transicionInvalida(TransicionDeEstadoInvalida e) {
        return problema(HttpStatus.CONFLICT, "Transicion de estado invalida", e);
    }

    @ExceptionHandler(ConflictoDeConcurrencia.class)
    public ProblemDetail conflicto(ConflictoDeConcurrencia e) {
        ProblemDetail detalle = problema(HttpStatus.CONFLICT, "Conflicto de concurrencia", e);
        detalle.setProperty("reintentable", true);
        return detalle;
    }

    /** Red de seguridad: un error de negocio sin mapeo explicito sale como 409, no como 500. */
    @ExceptionHandler(ExcepcionDeDominio.class)
    public ProblemDetail reglaDeNegocio(ExcepcionDeDominio e) {
        return problema(HttpStatus.CONFLICT, "Regla de negocio violada", e);
    }

    private static ProblemDetail problema(HttpStatus estado, String titulo, ExcepcionDeDominio e) {
        ProblemDetail detalle = ProblemDetail.forStatusAndDetail(estado, e.getMessage());
        detalle.setTitle(titulo);
        detalle.setProperties(new HashMap<>(Map.of("codigo", e.codigo())));
        return detalle;
    }
}
