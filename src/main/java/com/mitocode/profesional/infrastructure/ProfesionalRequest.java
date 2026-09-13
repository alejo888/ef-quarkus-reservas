package com.mitocode.profesional.infrastructure;

import jakarta.validation.constraints.NotBlank;

public record ProfesionalRequest(
        @NotBlank String nombres,
        @NotBlank String apellidos,
        @NotBlank String especialidad) {
}
