/**
 * Adaptadores del contexto Activos: persistencia Mongo, capa web y publicacion de eventos. Nada de
 * aqui puede ser importado desde {@code domain}.
 *
 * <p>El adaptador Mongo, cuando se implemente, debe usar bloqueo optimista y traducir el fallo de
 * version a {@link com.atlas.assetflow.shared.domain.ConflictoDeConcurrencia}: la invariante dentro
 * del agregado no protege de la concurrencia por si sola. El documento de persistencia es un modelo
 * aparte del agregado, para que el dominio no dependa de la base de datos.
 */
package com.atlas.assetflow.activos.infrastructure;
