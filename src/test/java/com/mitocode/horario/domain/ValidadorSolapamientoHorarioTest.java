package com.mitocode.horario.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.shared.domain.RangoHorario;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ValidadorSolapamientoHorarioTest {

    private final UUID profesionalId = UUID.randomUUID();
    private final LocalDate fecha = LocalDate.of(2026, 5, 10);

    @Test
    void noDeberiaLanzarExcepcionCuandoNoHayHorariosPrevios() {
        HorarioDisponible nuevo = horario(fecha, LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatCode(() -> ValidadorSolapamientoHorario.validar(nuevo, List.of()))
                .doesNotThrowAnyException();
    }

    @Test
    void noDeberiaLanzarExcepcionCuandoLosHorariosNoSeSolapan() {
        HorarioDisponible existente = horario(fecha, LocalTime.of(8, 0), LocalTime.of(9, 0));
        HorarioDisponible nuevo = horario(fecha, LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThatCode(() -> ValidadorSolapamientoHorario.validar(nuevo, List.of(existente)))
                .doesNotThrowAnyException();
    }

    @Test
    void deberiaLanzarExcepcionCuandoLosHorariosSeSolapanElMismoDia() {
        HorarioDisponible existente = horario(fecha, LocalTime.of(9, 0), LocalTime.of(11, 0));
        HorarioDisponible nuevo = horario(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatThrownBy(() -> ValidadorSolapamientoHorario.validar(nuevo, List.of(existente)))
                .isInstanceOf(HorarioSolapadoException.class);
    }

    @Test
    void noDeberiaLanzarExcepcionCuandoElSolapamientoEsEnOtraFecha() {
        HorarioDisponible existente = horario(fecha.plusDays(1), LocalTime.of(9, 0), LocalTime.of(11, 0));
        HorarioDisponible nuevo = horario(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatCode(() -> ValidadorSolapamientoHorario.validar(nuevo, List.of(existente)))
                .doesNotThrowAnyException();
    }

    @Test
    void noDeberiaLanzarExcepcionCuandoElSolapamientoEsConOtroProfesional() {
        HorarioDisponible deOtroProfesional = HorarioDisponible.crear(
                UUID.randomUUID(), fecha, new RangoHorario(LocalTime.of(9, 0), LocalTime.of(11, 0)));
        HorarioDisponible nuevo = horario(fecha, LocalTime.of(10, 0), LocalTime.of(12, 0));

        assertThatCode(() -> ValidadorSolapamientoHorario.validar(nuevo, List.of(deOtroProfesional)))
                .doesNotThrowAnyException();
    }

    private HorarioDisponible horario(LocalDate fecha, LocalTime inicio, LocalTime fin) {
        return HorarioDisponible.crear(profesionalId, fecha, new RangoHorario(inicio, fin));
    }
}
