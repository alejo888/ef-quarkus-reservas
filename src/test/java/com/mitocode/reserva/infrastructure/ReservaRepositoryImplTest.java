package com.mitocode.reserva.infrastructure;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.cliente.domain.Cliente;
import com.mitocode.cliente.infrastructure.ClienteRepositoryImpl;
import com.mitocode.profesional.domain.Profesional;
import com.mitocode.profesional.infrastructure.ProfesionalRepositoryImpl;
import com.mitocode.reserva.domain.Reserva;
import com.mitocode.reserva.domain.ReservaRepository;
import com.mitocode.reserva.domain.ReservaSolapadaException;
import com.mitocode.shared.domain.RangoHorario;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.vertx.VertxContextSupport;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Proves that the Postgres EXCLUDE USING gist constraint
 * (reserva_no_solapamiento_activa, see V2__reserva_no_solapamiento.sql) rejects
 * overlapping active reservations at the database level, independently of the
 * in-memory ValidadorCreacionReserva check — this test calls
 * ReservaRepository.guardar(...) directly, bypassing ReservaService and its
 * validator entirely.
 */
@QuarkusTest
class ReservaRepositoryImplTest {

    @Inject
    ReservaRepository reservaRepository;

    @Inject
    ReservaRepositoryImpl reservaRepositoryImpl;

    @Inject
    ClienteRepositoryImpl clienteRepository;

    @Inject
    ProfesionalRepositoryImpl profesionalRepository;

    private UUID clienteCreadoId;
    private UUID profesionalCreadoId;
    private UUID reservaCreadaId;

    @Test
    void guardarDeberiaLanzarReservaSolapadaExceptionPorLaRestriccionDeLaBaseDeDatos() throws Throwable {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.repo.test.%s@mail.com".formatted(UUID.randomUUID()),
                "999888777");
        VertxContextSupport.subscribeAndAwait(() -> clienteRepository.guardar(cliente));
        clienteCreadoId = cliente.getId();

        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");
        VertxContextSupport.subscribeAndAwait(() -> profesionalRepository.guardar(profesional));
        profesionalCreadoId = profesional.getId();

        LocalDate fecha = LocalDate.of(2026, 9, 1);

        Reserva primera = Reserva.crear(cliente.getId(), profesional.getId(), fecha,
                new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0)));
        VertxContextSupport.subscribeAndAwait(() -> reservaRepository.guardar(primera));
        reservaCreadaId = primera.getId();

        Reserva segunda = Reserva.crear(cliente.getId(), profesional.getId(), fecha,
                new RangoHorario(LocalTime.of(9, 30), LocalTime.of(10, 30)));

        assertThatThrownBy(() -> VertxContextSupport.subscribeAndAwait(() -> reservaRepository.guardar(segunda)))
                .isInstanceOf(ReservaSolapadaException.class);
    }

    @AfterEach
    void limpiarDatosCreados() throws Throwable {
        if (reservaCreadaId != null) {
            VertxContextSupport.subscribeAndAwait(
                    () -> Panache.withTransaction(() -> reservaRepositoryImpl.deleteById(reservaCreadaId)));
        }
        if (profesionalCreadoId != null) {
            VertxContextSupport.subscribeAndAwait(
                    () -> Panache.withTransaction(() -> profesionalRepository.deleteById(profesionalCreadoId)));
        }
        if (clienteCreadoId != null) {
            VertxContextSupport.subscribeAndAwait(
                    () -> Panache.withTransaction(() -> clienteRepository.deleteById(clienteCreadoId)));
        }
    }
}
