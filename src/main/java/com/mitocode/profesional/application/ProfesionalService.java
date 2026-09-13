package com.mitocode.profesional.application;

import com.mitocode.profesional.domain.Profesional;
import com.mitocode.profesional.domain.ProfesionalRepository;
import com.mitocode.reserva.domain.Reserva;
import com.mitocode.reserva.domain.ReservaRepository;
import com.mitocode.shared.exception.RecursoNoEncontradoException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.eclipse.microprofile.faulttolerance.Timeout;

@ApplicationScoped
public class ProfesionalService {

    @Inject
    ProfesionalRepository repository;

    @Inject
    ReservaRepository reservaRepository;

    public Uni<Profesional> crear(String nombres, String apellidos, String especialidad) {
        return repository.guardar(Profesional.crear(nombres, apellidos, especialidad));
    }

    public Uni<Profesional> obtenerPorId(UUID id) {
        return repository.buscarPorId(id)
                .onItem().ifNull().failWith(() -> new RecursoNoEncontradoException("Profesional", id));
    }

    public Uni<Profesional> actualizar(UUID id, String nombres, String apellidos, String especialidad) {
        return obtenerPorId(id)
                .invoke(profesional -> profesional.actualizarDatos(nombres, apellidos, especialidad))
                .chain(repository::actualizar);
    }

    public Uni<Void> eliminar(UUID id) {
        return obtenerPorId(id)
                .invoke(Profesional::desactivar)
                .chain(repository::actualizar)
                .replaceWithVoid();
    }

    @Timeout(2000)
    public Uni<List<ProfesionalConReservasActivas>> listarOrdenadosPorReservasActivas() {
        return repository.buscarTodos()
                .chain(profesionales -> reservaRepository.buscarTodas()
                        .map(reservas -> combinar(profesionales, reservas)));
    }

    private static List<ProfesionalConReservasActivas> combinar(List<Profesional> profesionales,
            List<Reserva> reservas) {
        Map<UUID, Long> conteoPorProfesional = reservas.stream()
                .filter(Reserva::estaActiva)
                .collect(Collectors.groupingBy(Reserva::getProfesionalId, Collectors.counting()));

        return profesionales.stream()
                .map(profesional -> new ProfesionalConReservasActivas(profesional,
                        conteoPorProfesional.getOrDefault(profesional.getId(), 0L)))
                .sorted(Comparator.comparingLong(ProfesionalConReservasActivas::reservasActivas).reversed())
                .toList();
    }
}
