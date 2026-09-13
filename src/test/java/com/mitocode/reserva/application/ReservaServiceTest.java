package com.mitocode.reserva.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.cliente.domain.Cliente;
import com.mitocode.cliente.domain.ClienteRepository;
import com.mitocode.horario.domain.HorarioDisponible;
import com.mitocode.horario.domain.HorarioDisponibleRepository;
import com.mitocode.profesional.domain.Profesional;
import com.mitocode.profesional.domain.ProfesionalRepository;
import com.mitocode.reserva.domain.DisponibilidadNoEncontradaException;
import com.mitocode.reserva.domain.EstadoReserva;
import com.mitocode.reserva.domain.Reserva;
import com.mitocode.reserva.domain.ReservaRepository;
import com.mitocode.reserva.domain.ReservaSolapadaException;
import com.mitocode.shared.domain.RangoHorario;
import com.mitocode.shared.exception.EntidadInactivaException;
import com.mitocode.shared.exception.RecursoNoEncontradoException;
import io.smallrye.mutiny.Uni;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReservaServiceTest {

    private final ClienteRepositoryFake clienteRepository = new ClienteRepositoryFake();
    private final ProfesionalRepositoryFake profesionalRepository = new ProfesionalRepositoryFake();
    private final HorarioDisponibleRepositoryFake horarioRepository = new HorarioDisponibleRepositoryFake();
    private final ReservaRepositoryFake reservaRepository = new ReservaRepositoryFake();
    private final ReservaService service = new ReservaService();

    private Cliente cliente;
    private Profesional profesional;
    private final LocalDate fecha = LocalDate.of(2026, 5, 10);

    @BeforeEach
    void setUp() {
        service.clienteRepository = clienteRepository;
        service.profesionalRepository = profesionalRepository;
        service.horarioRepository = horarioRepository;
        service.reservaRepository = reservaRepository;

        cliente = Cliente.crear("Ana", "Torres", "ana@mail.com", "999888777");
        profesional = Profesional.crear("Luis", "Salazar", "Psicologia");
        clienteRepository.agregar(cliente);
        profesionalRepository.agregar(profesional);
        horarioRepository.agregar(HorarioDisponible.crear(profesional.getId(), fecha,
                new RangoHorario(LocalTime.of(8, 0), LocalTime.of(12, 0))));
    }

    @Test
    void crearConDatosValidosPersisteLaReserva() {
        Reserva reserva = service
                .crear(cliente.getId(), profesional.getId(), fecha, LocalTime.of(9, 0), LocalTime.of(10, 0))
                .await().indefinitely();

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CREADA);
        assertThat(reservaRepository.datos).containsKey(reserva.getId());
    }

    @Test
    void crearConClienteInexistenteLanzaRecursoNoEncontradoException() {
        Uni<Reserva> uni = service.crear(UUID.randomUUID(), profesional.getId(), fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void crearConProfesionalInexistenteLanzaRecursoNoEncontradoException() {
        Uni<Reserva> uni = service.crear(cliente.getId(), UUID.randomUUID(), fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void crearConClienteInactivoLanzaEntidadInactivaException() {
        cliente.desactivar();

        Uni<Reserva> uni = service.crear(cliente.getId(), profesional.getId(), fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(EntidadInactivaException.class);
    }

    @Test
    void crearConProfesionalInactivoLanzaEntidadInactivaException() {
        profesional.desactivar();

        Uni<Reserva> uni = service.crear(cliente.getId(), profesional.getId(), fecha,
                LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(EntidadInactivaException.class);
    }

    @Test
    void crearSinHorarioQueLaCubraLanzaDisponibilidadNoEncontradaException() {
        Uni<Reserva> uni = service.crear(cliente.getId(), profesional.getId(), fecha,
                LocalTime.of(13, 0), LocalTime.of(14, 0));

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(DisponibilidadNoEncontradaException.class);
    }

    @Test
    void crearConSolapamientoDeReservaActivaLanzaReservaSolapadaException() {
        service.crear(cliente.getId(), profesional.getId(), fecha, LocalTime.of(9, 0), LocalTime.of(10, 0))
                .await().indefinitely();

        Uni<Reserva> uni = service.crear(cliente.getId(), profesional.getId(), fecha,
                LocalTime.of(9, 30), LocalTime.of(10, 30));

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(ReservaSolapadaException.class);
    }

    @Test
    void cancelarReservaExistenteCambiaEstadoYActualiza() {
        Reserva reserva = service
                .crear(cliente.getId(), profesional.getId(), fecha, LocalTime.of(9, 0), LocalTime.of(10, 0))
                .await().indefinitely();

        Reserva cancelada = service.cancelar(reserva.getId()).await().indefinitely();

        assertThat(cancelada.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        assertThat(reservaRepository.actualizaciones).contains(reserva.getId());
    }

    @Test
    void cancelarReservaInexistenteLanzaRecursoNoEncontradoException() {
        Uni<Reserva> uni = service.cancelar(UUID.randomUUID());

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    private static class ClienteRepositoryFake implements ClienteRepository {
        private final Map<UUID, Cliente> datos = new HashMap<>();

        void agregar(Cliente cliente) {
            datos.put(cliente.getId(), cliente);
        }

        @Override
        public Uni<Cliente> guardar(Cliente cliente) {
            agregar(cliente);
            return Uni.createFrom().item(cliente);
        }

        @Override
        public Uni<Cliente> buscarPorId(UUID id) {
            return Uni.createFrom().item(datos.get(id));
        }
    }

    private static class ProfesionalRepositoryFake implements ProfesionalRepository {
        private final Map<UUID, Profesional> datos = new HashMap<>();

        void agregar(Profesional profesional) {
            datos.put(profesional.getId(), profesional);
        }

        @Override
        public Uni<Profesional> guardar(Profesional profesional) {
            agregar(profesional);
            return Uni.createFrom().item(profesional);
        }

        @Override
        public Uni<Profesional> buscarPorId(UUID id) {
            return Uni.createFrom().item(datos.get(id));
        }
    }

    private static class HorarioDisponibleRepositoryFake implements HorarioDisponibleRepository {
        private final List<HorarioDisponible> datos = new ArrayList<>();

        void agregar(HorarioDisponible horario) {
            datos.add(horario);
        }

        @Override
        public Uni<HorarioDisponible> guardar(HorarioDisponible horario) {
            agregar(horario);
            return Uni.createFrom().item(horario);
        }

        @Override
        public Uni<List<HorarioDisponible>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha) {
            return Uni.createFrom().item(datos.stream()
                    .filter(h -> h.getProfesionalId().equals(profesionalId) && h.getFecha().equals(fecha))
                    .toList());
        }
    }

    private static class ReservaRepositoryFake implements ReservaRepository {
        private final Map<UUID, Reserva> datos = new HashMap<>();
        private final List<UUID> actualizaciones = new ArrayList<>();

        @Override
        public Uni<Reserva> guardar(Reserva reserva) {
            datos.put(reserva.getId(), reserva);
            return Uni.createFrom().item(reserva);
        }

        @Override
        public Uni<Reserva> actualizar(Reserva reserva) {
            actualizaciones.add(reserva.getId());
            datos.put(reserva.getId(), reserva);
            return Uni.createFrom().item(reserva);
        }

        @Override
        public Uni<List<Reserva>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha) {
            return Uni.createFrom().item(datos.values().stream()
                    .filter(r -> r.getProfesionalId().equals(profesionalId) && r.getFecha().equals(fecha))
                    .toList());
        }

        @Override
        public Uni<Reserva> buscarPorId(UUID id) {
            return Uni.createFrom().item(datos.get(id));
        }
    }
}
