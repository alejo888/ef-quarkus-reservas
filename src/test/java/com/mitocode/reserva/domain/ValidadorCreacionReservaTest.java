package com.mitocode.reserva.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.horario.domain.HorarioDisponible;
import com.mitocode.shared.domain.RangoHorario;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValidadorCreacionReservaTest {

    private final UUID clienteId = UUID.randomUUID();
    private final UUID profesionalId = UUID.randomUUID();
    private final LocalDate fecha = LocalDate.of(2026, 5, 10);

    @Test
    void deberiaLanzarExcepcionSiNoHayHorarioQueCubraElIntervalo() {
        Reserva nueva = reserva(LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatThrownBy(() -> ValidadorCreacionReserva.validar(nueva, List.of(), List.of()))
                .isInstanceOf(DisponibilidadNoEncontradaException.class);
    }

    @Test
    void deberiaLanzarExcepcionSiElHorarioQueCubreEstaDeshabilitado() {
        Reserva nueva = reserva(LocalTime.of(9, 0), LocalTime.of(10, 0));
        HorarioDisponible horario = horario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        horario.deshabilitar();

        assertThatThrownBy(() -> ValidadorCreacionReserva.validar(nueva, List.of(horario), List.of()))
                .isInstanceOf(DisponibilidadNoEncontradaException.class);
    }

    @Test
    void deberiaPermitirCuandoHayHorarioQueCubreYSinSolapamiento() {
        Reserva nueva = reserva(LocalTime.of(9, 0), LocalTime.of(10, 0));
        HorarioDisponible horario = horario(LocalTime.of(8, 0), LocalTime.of(12, 0));

        assertThatCode(() -> ValidadorCreacionReserva.validar(nueva, List.of(horario), List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void deberiaLanzarExcepcionSiSeSolapaConOtraReservaActivaDelProfesional() {
        Reserva nueva = reserva(LocalTime.of(9, 0), LocalTime.of(10, 0));
        HorarioDisponible horario = horario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Reserva existente = Reserva.crear(UUID.randomUUID(), profesionalId, fecha,
                new RangoHorario(LocalTime.of(9, 30), LocalTime.of(10, 30)));

        assertThatThrownBy(() -> ValidadorCreacionReserva.validar(nueva, List.of(horario), List.of(existente)))
                .isInstanceOf(ReservaSolapadaException.class);
    }

    @Test
    void noDeberiaLanzarExcepcionSiSeSolapaConUnaReservaCanceladaDelProfesional() {
        Reserva nueva = reserva(LocalTime.of(9, 0), LocalTime.of(10, 0));
        HorarioDisponible horario = horario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Reserva cancelada = Reserva.crear(UUID.randomUUID(), profesionalId, fecha,
                new RangoHorario(LocalTime.of(9, 30), LocalTime.of(10, 30)));
        cancelada.cancelar();

        assertThatCode(() -> ValidadorCreacionReserva.validar(nueva, List.of(horario), List.of(cancelada)))
                .doesNotThrowAnyException();
    }

    @Test
    void laCoberturaDeberiaIgnorarHorariosDeOtroProfesional() {
        Reserva nueva = reserva(LocalTime.of(9, 0), LocalTime.of(10, 0));
        HorarioDisponible horarioDeOtroProfesional = HorarioDisponible.crear(
                UUID.randomUUID(), fecha, new RangoHorario(LocalTime.of(8, 0), LocalTime.of(12, 0)));

        assertThatThrownBy(() -> ValidadorCreacionReserva.validar(nueva, List.of(horarioDeOtroProfesional), List.of()))
                .isInstanceOf(DisponibilidadNoEncontradaException.class);
    }

    @Test
    void elSolapamientoDeberiaIgnorarReservasDeOtroProfesional() {
        Reserva nueva = reserva(LocalTime.of(9, 0), LocalTime.of(10, 0));
        HorarioDisponible horario = horario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Reserva deOtroProfesional = Reserva.crear(UUID.randomUUID(), UUID.randomUUID(), fecha,
                new RangoHorario(LocalTime.of(9, 30), LocalTime.of(10, 30)));

        assertThatCode(() -> ValidadorCreacionReserva.validar(nueva, List.of(horario), List.of(deOtroProfesional)))
                .doesNotThrowAnyException();
    }

    @Test
    void noDeberiaLanzarExcepcionSiUnaReservaEmpiezaExactamenteCuandoOtraTermina() {
        Reserva nueva = reserva(LocalTime.of(10, 0), LocalTime.of(11, 0));
        HorarioDisponible horario = horario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Reserva existente = Reserva.crear(UUID.randomUUID(), profesionalId, fecha,
                new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0)));

        assertThatCode(() -> ValidadorCreacionReserva.validar(nueva, List.of(horario), List.of(existente)))
                .doesNotThrowAnyException();
    }

    @Test
    void noDeberiaLanzarExcepcionSiUnaReservaTerminaExactamenteCuandoOtraEmpieza() {
        Reserva nueva = reserva(LocalTime.of(8, 0), LocalTime.of(9, 0));
        HorarioDisponible horario = horario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        Reserva existente = Reserva.crear(UUID.randomUUID(), profesionalId, fecha,
                new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0)));

        assertThatCode(() -> ValidadorCreacionReserva.validar(nueva, List.of(horario), List.of(existente)))
                .doesNotThrowAnyException();
    }

    private Reserva reserva(LocalTime inicio, LocalTime fin) {
        return Reserva.crear(clienteId, profesionalId, fecha, new RangoHorario(inicio, fin));
    }

    private HorarioDisponible horario(LocalTime inicio, LocalTime fin) {
        return HorarioDisponible.crear(profesionalId, fecha, new RangoHorario(inicio, fin));
    }
}
