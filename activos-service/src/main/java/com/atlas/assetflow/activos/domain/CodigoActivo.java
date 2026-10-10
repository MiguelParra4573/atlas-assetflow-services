package com.atlas.assetflow.activos.domain;

import java.util.regex.Pattern;

/**
 * Value object: codigo de negocio unico de un {@link Activo}.
 *
 * <p>Formato: prefijo de familia de equipo en mayusculas, de 2 a 4 letras, guion, y de 3 a 4
 * digitos. Validos: {@code CMP-204}, {@code BMB-0117}, {@code MT-998}.
 *
 * <p>TODO (Fase 2): implementar. Inmutable, con igualdad por valor, validando {@link #PATRON} en el
 * constructor y lanzando {@link CodigoActivoInvalido} si no cumple: un codigo invalido no debe
 * llegar a existir como objeto.
 */
public final class CodigoActivo {

    /** Prefijo de familia de 2 a 4 letras mayusculas, guion, 3 o 4 digitos. */
    public static final Pattern PATRON = Pattern.compile("^[A-Z]{2,4}-\\d{3,4}$");
}
