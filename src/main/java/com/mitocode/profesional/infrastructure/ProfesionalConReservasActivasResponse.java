package com.mitocode.profesional.infrastructure;

import com.mitocode.profesional.application.ProfesionalConReservasActivas;
import java.util.UUID;

public record ProfesionalConReservasActivasResponse(
        UUID id,
        String nombres,
        String apellidos,
        String especialidad,
        boolean estadoActivo,
        long reservasActivas) {

    public static ProfesionalConReservasActivasResponse from(ProfesionalConReservasActivas proyeccion) {
        return new ProfesionalConReservasActivasResponse(
                proyeccion.profesional().getId(),
                proyeccion.profesional().getNombres(),
                proyeccion.profesional().getApellidos(),
                proyeccion.profesional().getEspecialidad(),
                proyeccion.profesional().isEstadoActivo(),
                proyeccion.reservasActivas());
    }
}
