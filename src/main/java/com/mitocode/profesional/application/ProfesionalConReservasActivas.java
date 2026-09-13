package com.mitocode.profesional.application;

import com.mitocode.profesional.domain.Profesional;

/**
 * Internal projection pairing a Profesional with its count of active
 * (non-cancelled) reservas. Not a REST DTO.
 */
public record ProfesionalConReservasActivas(Profesional profesional, long reservasActivas) {
}
