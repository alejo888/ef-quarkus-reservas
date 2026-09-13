package com.mitocode.reserva.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.shared.domain.RangoHorario;
import com.mitocode.shared.exception.CampoRequeridoException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ReservaTest {

    private final UUID clienteId = UUID.randomUUID();
    private final UUID profesionalId = UUID.randomUUID();
    private final LocalDate fecha = LocalDate.of(2026, 5, 10);
    private final RangoHorario rango = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));

    @Test
    void deberiaCrearseConEstadoCreada() {
        Reserva reserva = Reserva.crear(clienteId, profesionalId, fecha, rango);

        assertThat(reserva.getId()).isNotNull();
        assertThat(reserva.getClienteId()).isEqualTo(clienteId);
        assertThat(reserva.getProfesionalId()).isEqualTo(profesionalId);
        assertThat(reserva.getFecha()).isEqualTo(fecha);
        assertThat(reserva.getRango()).isEqualTo(rango);
        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CREADA);
    }

    @Test
    void deberiaRechazarClienteIdNulo() {
        assertThatThrownBy(() -> Reserva.crear(null, profesionalId, fecha, rango))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarProfesionalIdNulo() {
        assertThatThrownBy(() -> Reserva.crear(clienteId, null, fecha, rango))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarFechaNula() {
        assertThatThrownBy(() -> Reserva.crear(clienteId, profesionalId, null, rango))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarRangoNulo() {
        assertThatThrownBy(() -> Reserva.crear(clienteId, profesionalId, fecha, null))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void cancelarDeberiaCambiarEstadoACancelada() {
        Reserva reserva = Reserva.crear(clienteId, profesionalId, fecha, rango);

        reserva.cancelar();

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
    }

    @Test
    void cancelarUnaReservaYaCanceladaDeberiaLanzarExcepcion() {
        Reserva reserva = Reserva.crear(clienteId, profesionalId, fecha, rango);
        reserva.cancelar();

        assertThatThrownBy(reserva::cancelar)
                .isInstanceOf(EstadoReservaInvalidoException.class);
    }

    @Test
    void estaActivaDeberiaSerTrueSoloCuandoEstaCreada() {
        Reserva reserva = Reserva.crear(clienteId, profesionalId, fecha, rango);
        assertThat(reserva.estaActiva()).isTrue();

        reserva.cancelar();
        assertThat(reserva.estaActiva()).isFalse();
    }
}
