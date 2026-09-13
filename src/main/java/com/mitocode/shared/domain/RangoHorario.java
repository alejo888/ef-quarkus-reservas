package com.mitocode.shared.domain;

import java.time.LocalTime;

/**
 * Time-of-day interval shared by HorarioDisponible and Reserva. Encapsulates
 * the overlap and coverage checks so both bounded contexts reuse the same
 * algorithm instead of duplicating it.
 */
public record RangoHorario(LocalTime inicio, LocalTime fin) {

    public RangoHorario {
        if (inicio == null || fin == null || !inicio.isBefore(fin)) {
            throw new RangoHorarioInvalidoException(inicio, fin);
        }
    }

    public boolean seSolapaCon(RangoHorario otro) {
        return inicio.isBefore(otro.fin) && otro.inicio.isBefore(fin);
    }

    public boolean cubre(RangoHorario otro) {
        return !otro.inicio.isBefore(inicio) && !otro.fin.isAfter(fin);
    }
}
