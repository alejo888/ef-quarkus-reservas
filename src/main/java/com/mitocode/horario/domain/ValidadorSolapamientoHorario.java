package com.mitocode.horario.domain;

import java.util.List;

/**
 * Enforces the "no overlapping availability slots for the same professional"
 * rule. Defensively re-filters by profesionalId/fecha so it stays correct
 * even if the caller passes a broader candidate list than expected.
 */
public final class ValidadorSolapamientoHorario {

    private ValidadorSolapamientoHorario() {
    }

    public static void validar(HorarioDisponible nuevo, List<HorarioDisponible> horariosExistentes) {
        boolean seSolapa = horariosExistentes.stream()
                .filter(existente -> existente.getProfesionalId().equals(nuevo.getProfesionalId()))
                .filter(existente -> existente.getFecha().equals(nuevo.getFecha()))
                .anyMatch(existente -> existente.getRango().seSolapaCon(nuevo.getRango()));

        if (seSolapa) {
            throw new HorarioSolapadoException(nuevo.getProfesionalId(), nuevo.getFecha());
        }
    }
}
