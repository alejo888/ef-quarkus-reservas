package com.mitocode.cliente.domain;

import io.smallrye.mutiny.Uni;
import java.util.UUID;

/**
 * Repository port for the Cliente aggregate. Implemented in the
 * infrastructure layer with Hibernate Reactive Panache.
 */
public interface ClienteRepository {

    /** Fails with EmailDuplicadoException when the email is already taken. */
    Uni<Cliente> guardar(Cliente cliente);

    /** Emits {@code null} when no cliente exists with the given id. */
    Uni<Cliente> buscarPorId(UUID id);
}
