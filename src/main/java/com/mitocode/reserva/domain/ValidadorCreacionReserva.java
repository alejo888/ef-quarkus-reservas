package com.mitocode.reserva.domain;

import com.mitocode.horario.domain.HorarioDisponible;
import java.util.List;

/**
 * Enforces the two pure creation rules for Reserva: the requested interval
 * must be covered by an enabled availability slot, and it must not overlap
 * another active reservation of the same professional. Both lists are
 * defensively re-filtered by profesionalId/fecha, same approach as
 * ValidadorSolapamientoHorario.
 *
 * <p>The "cliente y profesional deben estar activos" rule is NOT enforced
 * here: it requires loading the Cliente/Profesional aggregates, which is an
 * application-layer concern, not pure domain logic.
 */
public final class ValidadorCreacionReserva {

    private ValidadorCreacionReserva() {
    }

    public static void validar(Reserva nueva, List<HorarioDisponible> horariosDelProfesional,
            List<Reserva> reservasDelProfesional) {
        boolean estaCubierta = horariosDelProfesional.stream()
                .filter(horario -> horario.getProfesionalId().equals(nueva.getProfesionalId()))
                .filter(horario -> horario.getFecha().equals(nueva.getFecha()))
                .filter(HorarioDisponible::isEstado)
                .anyMatch(horario -> horario.getRango().cubre(nueva.getRango()));

        if (!estaCubierta) {
            throw new DisponibilidadNoEncontradaException(nueva.getProfesionalId(), nueva.getFecha());
        }

        boolean seSolapa = reservasDelProfesional.stream()
                .filter(reserva -> reserva.getProfesionalId().equals(nueva.getProfesionalId()))
                .filter(reserva -> reserva.getFecha().equals(nueva.getFecha()))
                .filter(Reserva::estaActiva)
                .anyMatch(reserva -> reserva.getRango().seSolapaCon(nueva.getRango()));

        if (seSolapa) {
            throw new ReservaSolapadaException(nueva.getProfesionalId(), nueva.getFecha());
        }
    }
}
