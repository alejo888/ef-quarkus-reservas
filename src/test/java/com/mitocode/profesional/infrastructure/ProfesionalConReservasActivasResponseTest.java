package com.mitocode.profesional.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import com.mitocode.profesional.application.ProfesionalConReservasActivas;
import com.mitocode.profesional.domain.Profesional;
import org.junit.jupiter.api.Test;

class ProfesionalConReservasActivasResponseTest {

    @Test
    void fromDeberiaMapearTodosLosCamposDeLaProyeccion() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");
        ProfesionalConReservasActivas proyeccion = new ProfesionalConReservasActivas(profesional, 3L);

        ProfesionalConReservasActivasResponse response = ProfesionalConReservasActivasResponse.from(proyeccion);

        assertThat(response.id()).isEqualTo(profesional.getId());
        assertThat(response.nombres()).isEqualTo(profesional.getNombres());
        assertThat(response.apellidos()).isEqualTo(profesional.getApellidos());
        assertThat(response.especialidad()).isEqualTo(profesional.getEspecialidad());
        assertThat(response.estadoActivo()).isEqualTo(profesional.isEstadoActivo());
        assertThat(response.reservasActivas()).isEqualTo(3L);
    }
}
