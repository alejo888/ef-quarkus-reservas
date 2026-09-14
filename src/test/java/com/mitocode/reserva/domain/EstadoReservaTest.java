package com.mitocode.reserva.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Guards the literal string 'CREADA' hardcoded in the WHERE clause of the
 * Postgres EXCLUDE constraint (V2__reserva_no_solapamiento.sql). The
 * migration has no compile-time link to this enum, so renaming CREADA
 * would silently disable the no-overlap protection for active reservas
 * unless this test catches it first.
 */
class EstadoReservaTest {

    @Test
    void elNombreDeCreadaDebeCoincidirConElUsadoEnLaMigracionDeLaBaseDeDatos() {
        assertThat(EstadoReserva.CREADA.name()).isEqualTo("CREADA");
    }
}
