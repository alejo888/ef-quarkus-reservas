package com.mitocode.cliente.application;

import com.mitocode.cliente.domain.Cliente;
import com.mitocode.cliente.domain.ClienteRepository;
import com.mitocode.shared.exception.RecursoNoEncontradoException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class ClienteService {

    @Inject
    ClienteRepository repository;

    public Uni<Cliente> crear(String nombres, String apellidos, String email, String telefono) {
        return repository.guardar(Cliente.crear(nombres, apellidos, email, telefono));
    }

    public Uni<Cliente> obtenerPorId(UUID id) {
        return repository.buscarPorId(id)
                .onItem().ifNull().failWith(() -> new RecursoNoEncontradoException("Cliente", id));
    }
}
