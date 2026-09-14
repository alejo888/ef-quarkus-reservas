package com.mitocode.profesional.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.shared.exception.CampoRequeridoException;
import org.junit.jupiter.api.Test;

class ProfesionalTest {

    @Test
    void deberiaCrearseActivoYConIdGenerado() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");

        assertThat(profesional.getId()).isNotNull();
        assertThat(profesional.getNombres()).isEqualTo("Luis");
        assertThat(profesional.getApellidos()).isEqualTo("Salazar");
        assertThat(profesional.getEspecialidad()).isEqualTo("Psicologia");
        assertThat(profesional.isEstadoActivo()).isTrue();
    }

    @Test
    void deberiaRechazarNombresVacios() {
        assertThatThrownBy(() -> Profesional.crear(" ", "Salazar", "Psicologia"))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarApellidosNulos() {
        assertThatThrownBy(() -> Profesional.crear("Luis", null, "Psicologia"))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarEspecialidadVacia() {
        assertThatThrownBy(() -> Profesional.crear("Luis", "Salazar", ""))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void desactivarDeberiaCambiarEstadoActivoAFalse() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");

        profesional.desactivar();

        assertThat(profesional.isEstadoActivo()).isFalse();
    }

    @Test
    void activarDeberiaReactivarUnProfesionalDesactivado() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");
        profesional.desactivar();

        profesional.activar();

        assertThat(profesional.isEstadoActivo()).isTrue();
    }

    @Test
    void actualizarDatosDeberiaReasignarLosCampos() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");

        profesional.actualizarDatos("Carlos", "Mendez", "Nutricion");

        assertThat(profesional.getNombres()).isEqualTo("Carlos");
        assertThat(profesional.getApellidos()).isEqualTo("Mendez");
        assertThat(profesional.getEspecialidad()).isEqualTo("Nutricion");
    }

    @Test
    void actualizarDatosDeberiaRechazarNombresVacios() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");

        assertThatThrownBy(() -> profesional.actualizarDatos(" ", "Mendez", "Nutricion"))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void actualizarDatosDeberiaRechazarApellidosVacios() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");

        assertThatThrownBy(() -> profesional.actualizarDatos("Carlos", " ", "Nutricion"))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void actualizarDatosDeberiaRechazarEspecialidadVacia() {
        Profesional profesional = Profesional.crear("Luis", "Salazar", "Psicologia");

        assertThatThrownBy(() -> profesional.actualizarDatos("Carlos", "Mendez", " "))
                .isInstanceOf(CampoRequeridoException.class);
    }
}
