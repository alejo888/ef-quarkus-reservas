package com.mitocode.cliente.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.shared.exception.CampoRequeridoException;
import org.junit.jupiter.api.Test;

class ClienteTest {

    @Test
    void deberiaCrearseActivoYConIdGenerado() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");

        assertThat(cliente.getId()).isNotNull();
        assertThat(cliente.getNombres()).isEqualTo("Ana");
        assertThat(cliente.getApellidos()).isEqualTo("Torres");
        assertThat(cliente.getEmail()).isEqualTo("ana.torres@mail.com");
        assertThat(cliente.getTelefono()).isEqualTo("999888777");
        assertThat(cliente.isEstadoActivo()).isTrue();
    }

    @Test
    void deberiaRechazarNombresVacios() {
        assertThatThrownBy(() -> Cliente.crear(" ", "Torres", "ana.torres@mail.com", "999888777"))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarApellidosNulos() {
        assertThatThrownBy(() -> Cliente.crear("Ana", null, "ana.torres@mail.com", "999888777"))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarTelefonoVacio() {
        assertThatThrownBy(() -> Cliente.crear("Ana", "Torres", "ana.torres@mail.com", ""))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void deberiaRechazarEmailVacio() {
        assertThatThrownBy(() -> Cliente.crear("Ana", "Torres", " ", "999888777"))
                .isInstanceOf(EmailInvalidoException.class);
    }

    @Test
    void deberiaRechazarEmailConFormatoInvalido() {
        assertThatThrownBy(() -> Cliente.crear("Ana", "Torres", "no-es-un-email", "999888777"))
                .isInstanceOf(EmailInvalidoException.class);
    }

    @Test
    void desactivarDeberiaCambiarEstadoActivoAFalse() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");

        cliente.desactivar();

        assertThat(cliente.isEstadoActivo()).isFalse();
    }

    @Test
    void activarDeberiaReactivarUnClienteDesactivado() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");
        cliente.desactivar();

        cliente.activar();

        assertThat(cliente.isEstadoActivo()).isTrue();
    }

    @Test
    void actualizarDatosDeberiaReasignarLosCampos() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");

        cliente.actualizarDatos("Maria", "Gomez", "maria.gomez@mail.com", "111222333");

        assertThat(cliente.getNombres()).isEqualTo("Maria");
        assertThat(cliente.getApellidos()).isEqualTo("Gomez");
        assertThat(cliente.getEmail()).isEqualTo("maria.gomez@mail.com");
        assertThat(cliente.getTelefono()).isEqualTo("111222333");
    }

    @Test
    void actualizarDatosDeberiaRechazarNombresVacios() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");

        assertThatThrownBy(() -> cliente.actualizarDatos(" ", "Gomez", "maria.gomez@mail.com", "111222333"))
                .isInstanceOf(CampoRequeridoException.class);
    }

    @Test
    void actualizarDatosDeberiaRechazarEmailConFormatoInvalido() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");

        assertThatThrownBy(() -> cliente.actualizarDatos("Maria", "Gomez", "no-es-un-email", "111222333"))
                .isInstanceOf(EmailInvalidoException.class);
    }
}
