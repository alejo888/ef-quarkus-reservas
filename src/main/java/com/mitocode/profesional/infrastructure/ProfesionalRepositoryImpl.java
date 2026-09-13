package com.mitocode.profesional.infrastructure;

import com.mitocode.profesional.domain.Profesional;
import com.mitocode.profesional.domain.ProfesionalRepository;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ProfesionalRepositoryImpl implements ProfesionalRepository, PanacheRepositoryBase<Profesional, UUID> {

    @Override
    public Uni<Profesional> guardar(Profesional profesional) {
        return Panache.withTransaction(() -> persist(profesional)).replaceWith(profesional);
    }

    @Override
    public Uni<Profesional> buscarPorId(UUID id) {
        return findById(id);
    }

    @Override
    public Uni<List<Profesional>> buscarTodos() {
        return listAll();
    }
}
