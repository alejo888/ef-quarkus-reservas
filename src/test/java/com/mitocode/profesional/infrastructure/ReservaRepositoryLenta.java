package com.mitocode.profesional.infrastructure;

import com.mitocode.reserva.domain.Reserva;
import com.mitocode.reserva.domain.ReservaRepository;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Test-only ReservaRepository that delays buscarTodas() beyond the 2s
 * @Timeout on ProfesionalService.listarOrdenadosPorReservasActivas(), so
 * tests can deterministically prove the timeout is actually enforced.
 */
@Alternative
@ApplicationScoped
public class ReservaRepositoryLenta implements ReservaRepository {

    @Override
    public Uni<Reserva> guardar(Reserva reserva) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uni<Reserva> actualizar(Reserva reserva) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uni<List<Reserva>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uni<Reserva> buscarPorId(UUID id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Uni<List<Reserva>> buscarTodas() {
        return Uni.createFrom().item(List.<Reserva>of())
                .onItem().delayIt().by(Duration.ofSeconds(3));
    }
}
