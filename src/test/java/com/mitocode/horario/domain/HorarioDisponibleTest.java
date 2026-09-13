package com.mitocode.horario.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.shared.domain.RangoHorario;
import com.mitocode.shared.exception.CampoRequeridoException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class HorarioDisponibleTest {

    private final UUID profesionalId = UUID.randomUUID();
    private final LocalDate fecha = LocalDate.of(2026, 5, 10);
    private final RangoHorario rango = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));

    @Test
    void deberiaCrearseActivoYConIdGenerado() {
        HorarioDisponible horario = HorarioDisponible.crear(profesionalId, fecha, rango);

        assertThat(horario.getId()).isNotNull();
        assertThat(horario.getProfesionalId()).isEqualTo(profesionalId);
        assertThat(horario.getFecha()).isEqualTo(fecha);
        assertThat(horario.getRango()).isEqualTo(rango);
        assertThat(horario.isEstado()).isTrue();
    }

    @Test
    void deberiaRechazarProfesionalIdNulo() {
        assertThatThrownBy(() -> HorarioDisponible.crear(null, fecha, rango))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarFechaNula() {
        assertThatThrownBy(() -> HorarioDisponible.crear(profesionalId, null, rango))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarRangoNulo() {
        assertThatThrownBy(() -> HorarioDisponible.crear(profesionalId, fecha, null))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deshabilitarDeberiaCambiarEstadoAFalse() {
        HorarioDisponible horario = HorarioDisponible.crear(profesionalId, fecha, rango);

        horario.deshabilitar();

        assertThat(horario.isEstado()).isFalse();
    }
}
