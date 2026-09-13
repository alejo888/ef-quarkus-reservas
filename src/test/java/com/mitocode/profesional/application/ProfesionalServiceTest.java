package com.mitocode.profesional.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.mitocode.profesional.domain.Profesional;
import com.mitocode.profesional.domain.ProfesionalRepository;
import com.mitocode.reserva.domain.Reserva;
import com.mitocode.reserva.domain.ReservaRepository;
import com.mitocode.shared.domain.RangoHorario;
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

class ProfesionalServiceTest {

    private final ProfesionalRepositoryFake profesionalRepository = new ProfesionalRepositoryFake();
    private final ReservaRepositoryFake reservaRepository = new ReservaRepositoryFake();
    private final ProfesionalService service = new ProfesionalService();

    private final LocalDate fecha = LocalDate.of(2026, 5, 10);
    private final RangoHorario rango = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));

    @BeforeEach
    void setUp() {
        service.repository = profesionalRepository;
        service.reservaRepository = reservaRepository;
    }

    @Test
    void listarOrdenadosPorReservasActivasOrdenaDescendentePorCantidad() {
        Profesional conDos = Profesional.crear("Luis", "Salazar", "Psicologia");
        Profesional conUna = Profesional.crear("Ana", "Reyes", "Nutricion");
        profesionalRepository.agregar(conDos);
        profesionalRepository.agregar(conUna);

        reservaRepository.agregar(reservaActivaPara(conDos.getId()));
        reservaRepository.agregar(reservaActivaPara(conDos.getId()));
        reservaRepository.agregar(reservaActivaPara(conUna.getId()));

        List<ProfesionalConReservasActivas> resultado = service.listarOrdenadosPorReservasActivas()
                .await().indefinitely();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).profesional().getId()).isEqualTo(conDos.getId());
        assertThat(resultado.get(0).reservasActivas()).isEqualTo(2);
        assertThat(resultado.get(1).profesional().getId()).isEqualTo(conUna.getId());
        assertThat(resultado.get(1).reservasActivas()).isEqualTo(1);
    }

    @Test
    void listarOrdenadosPorReservasActivasUbicaSinReservasAlFinalConCeroConteo() {
        Profesional conReservas = Profesional.crear("Luis", "Salazar", "Psicologia");
        Profesional sinReservas = Profesional.crear("Ana", "Reyes", "Nutricion");
        profesionalRepository.agregar(conReservas);
        profesionalRepository.agregar(sinReservas);

        reservaRepository.agregar(reservaActivaPara(conReservas.getId()));

        List<ProfesionalConReservasActivas> resultado = service.listarOrdenadosPorReservasActivas()
                .await().indefinitely();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(1).profesional().getId()).isEqualTo(sinReservas.getId());
        assertThat(resultado.get(1).reservasActivas()).isZero();
    }

    @Test
    void listarOrdenadosPorReservasActivasNoCuentaReservasCanceladas() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");
        profesionalRepository.agregar(profesional);

        Reserva activa = reservaActivaPara(profesional.getId());
        Reserva cancelada = reservaActivaPara(profesional.getId());
        cancelada.cancelar();
        reservaRepository.agregar(activa);
        reservaRepository.agregar(cancelada);

        List<ProfesionalConReservasActivas> resultado = service.listarOrdenadosPorReservasActivas()
                .await().indefinitely();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).reservasActivas()).isEqualTo(1);
    }

    private Reserva reservaActivaPara(UUID profesionalId) {
        return Reserva.crear(UUID.randomUUID(), profesionalId, fecha, rango);
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

        @Override
        public Uni<List<Profesional>> buscarTodos() {
            return Uni.createFrom().item(List.copyOf(datos.values()));
        }
    }

    private static class ReservaRepositoryFake implements ReservaRepository {
        private final List<Reserva> datos = new ArrayList<>();

        void agregar(Reserva reserva) {
            datos.add(reserva);
        }

        @Override
        public Uni<Reserva> guardar(Reserva reserva) {
            agregar(reserva);
            return Uni.createFrom().item(reserva);
        }

        @Override
        public Uni<Reserva> actualizar(Reserva reserva) {
            return Uni.createFrom().item(reserva);
        }

        @Override
        public Uni<List<Reserva>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha) {
            return Uni.createFrom().item(datos.stream()
                    .filter(r -> r.getProfesionalId().equals(profesionalId) && r.getFecha().equals(fecha))
                    .toList());
        }

        @Override
        public Uni<Reserva> buscarPorId(UUID id) {
            return Uni.createFrom().item(datos.stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null));
        }

        @Override
        public Uni<List<Reserva>> buscarTodas() {
            return Uni.createFrom().item(List.copyOf(datos));
        }
    }
}
