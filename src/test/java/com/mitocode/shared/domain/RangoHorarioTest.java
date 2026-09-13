package com.mitocode.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class RangoHorarioTest {

    @Test
    void deberiaCrearseCuandoInicioEsAnteriorAFin() {
        RangoHorario rango = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThat(rango.inicio()).isEqualTo(LocalTime.of(9, 0));
        assertThat(rango.fin()).isEqualTo(LocalTime.of(10, 0));
    }

    @Test
    void deberiaRechazarInicioIgualAFin() {
        LocalTime hora = LocalTime.of(9, 0);

        assertThatThrownBy(() -> new RangoHorario(hora, hora))
                .isInstanceOf(RangoHorarioInvalidoException.class);
    }

    @Test
    void deberiaRechazarInicioPosteriorAFin() {
        assertThatThrownBy(() -> new RangoHorario(LocalTime.of(10, 0), LocalTime.of(9, 0)))
                .isInstanceOf(RangoHorarioInvalidoException.class);
    }

    @Test
    void deberiaDetectarSolapamientoParcial() {
        RangoHorario rango = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));
        RangoHorario otro = new RangoHorario(LocalTime.of(9, 30), LocalTime.of(10, 30));

        assertThat(rango.seSolapaCon(otro)).isTrue();
        assertThat(otro.seSolapaCon(rango)).isTrue();
    }

    @Test
    void deberiaDetectarSolapamientoCuandoUnoContieneAlOtro() {
        RangoHorario contenedor = new RangoHorario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        RangoHorario contenido = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));

        assertThat(contenedor.seSolapaCon(contenido)).isTrue();
    }

    @Test
    void noDeberiaSolaparCuandoSonAdyacentes() {
        RangoHorario rango = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));
        RangoHorario otro = new RangoHorario(LocalTime.of(10, 0), LocalTime.of(11, 0));

        assertThat(rango.seSolapaCon(otro)).isFalse();
        assertThat(otro.seSolapaCon(rango)).isFalse();
    }

    @Test
    void noDeberiaSolaparCuandoEstanSeparados() {
        RangoHorario rango = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(10, 0));
        RangoHorario otro = new RangoHorario(LocalTime.of(14, 0), LocalTime.of(15, 0));

        assertThat(rango.seSolapaCon(otro)).isFalse();
    }

    @Test
    void deberiaCubrirUnRangoIgualOInternoIncluyendoLosBordes() {
        RangoHorario disponible = new RangoHorario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        RangoHorario solicitado = new RangoHorario(LocalTime.of(8, 0), LocalTime.of(12, 0));
        RangoHorario solicitadoInterno = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(11, 0));

        assertThat(disponible.cubre(solicitado)).isTrue();
        assertThat(disponible.cubre(solicitadoInterno)).isTrue();
    }

    @Test
    void noDeberiaCubrirUnRangoQueSeExtiendeFueraDeLosLimites() {
        RangoHorario disponible = new RangoHorario(LocalTime.of(9, 0), LocalTime.of(12, 0));
        RangoHorario solicitado = new RangoHorario(LocalTime.of(8, 0), LocalTime.of(11, 0));

        assertThat(disponible.cubre(solicitado)).isFalse();
    }
}
