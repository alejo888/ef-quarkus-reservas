-- Prevents overlapping active reservations for the same professional at the
-- database level, closing the check-then-act race that an in-memory-only
-- validation (ValidadorCreacionReserva) cannot guarantee under concurrency.
CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE reserva
    ADD CONSTRAINT reserva_no_solapamiento_activa
    EXCLUDE USING gist (
        profesional_id WITH =,
        tsrange(fecha + hora_inicio, fecha + hora_fin) WITH &&
    )
    WHERE (estado = 'CREADA');
