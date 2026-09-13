CREATE TABLE profesional (
    id             UUID PRIMARY KEY,
    nombres        VARCHAR(100) NOT NULL,
    apellidos      VARCHAR(100) NOT NULL,
    especialidad   VARCHAR(100) NOT NULL,
    estado_activo  BOOLEAN      NOT NULL DEFAULT TRUE
);

CREATE TABLE cliente (
    id             UUID PRIMARY KEY,
    nombres        VARCHAR(100) NOT NULL,
    apellidos      VARCHAR(100) NOT NULL,
    email          VARCHAR(150) NOT NULL,
    telefono       VARCHAR(30)  NOT NULL,
    estado_activo  BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_cliente_email UNIQUE (email)
);

CREATE TABLE horario_disponible (
    id              UUID PRIMARY KEY,
    profesional_id  UUID         NOT NULL REFERENCES profesional (id),
    fecha           DATE         NOT NULL,
    hora_inicio     TIME         NOT NULL,
    hora_fin        TIME         NOT NULL,
    estado          BOOLEAN      NOT NULL DEFAULT TRUE,
    CONSTRAINT ck_horario_rango CHECK (hora_inicio < hora_fin)
);

-- Supports the per-professional/day range scan used to enforce the
-- "no overlapping availability slots" rule.
CREATE INDEX ix_horario_profesional_fecha ON horario_disponible (profesional_id, fecha);

CREATE TABLE reserva (
    id              UUID PRIMARY KEY,
    fecha           DATE         NOT NULL,
    hora_inicio     TIME         NOT NULL,
    hora_fin        TIME         NOT NULL,
    cliente_id      UUID         NOT NULL REFERENCES cliente (id),
    profesional_id  UUID         NOT NULL REFERENCES profesional (id),
    estado          VARCHAR(20)  NOT NULL,
    CONSTRAINT ck_reserva_rango CHECK (hora_inicio < hora_fin),
    CONSTRAINT ck_reserva_estado CHECK (estado IN ('CREADA', 'CANCELADA', 'COMPLETADA'))
);

-- Supports the per-professional/day overlap check on active reservations,
-- and the functional grouping-by-date and count-by-professional queries.
CREATE INDEX ix_reserva_profesional_fecha ON reserva (profesional_id, fecha);
CREATE INDEX ix_reserva_fecha ON reserva (fecha);
