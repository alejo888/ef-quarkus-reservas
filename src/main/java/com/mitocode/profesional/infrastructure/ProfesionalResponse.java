package com.mitocode.profesional.infrastructure;

import com.mitocode.profesional.domain.Profesional;
import java.util.UUID;

public record ProfesionalResponse(
        UUID id,
        String nombres,
        String apellidos,
        String especialidad,
        boolean estadoActivo) {

    public static ProfesionalResponse from(Profesional profesional) {
        return new ProfesionalResponse(
                profesional.getId(),
                profesional.getNombres(),
                profesional.getApellidos(),
                profesional.getEspecialidad(),
                profesional.isEstadoActivo());
    }
}
