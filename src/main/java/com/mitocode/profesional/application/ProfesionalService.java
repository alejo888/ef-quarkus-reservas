package com.mitocode.profesional.application;

import com.mitocode.profesional.domain.Profesional;
import com.mitocode.profesional.domain.ProfesionalRepository;
import com.mitocode.shared.exception.RecursoNoEncontradoException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.UUID;

@ApplicationScoped
public class ProfesionalService {

    @Inject
    ProfesionalRepository repository;

    public Uni<Profesional> crear(String nombres, String apellidos, String especialidad) {
        return repository.guardar(Profesional.crear(nombres, apellidos, especialidad));
    }

    public Uni<Profesional> obtenerPorId(UUID id) {
        return repository.buscarPorId(id)
                .onItem().ifNull().failWith(() -> new RecursoNoEncontradoException("Profesional", id));
    }
}
