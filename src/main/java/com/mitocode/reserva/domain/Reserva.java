package com.mitocode.reserva.domain;

import com.mitocode.shared.domain.RangoHorario;
import com.mitocode.shared.exception.CampoRequeridoException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "reserva")
public class Reserva {

    @Id
    private UUID id;

    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    @Column(name = "cliente_id")
    private UUID clienteId;

    @Column(name = "profesional_id")
    private UUID profesionalId;

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    protected Reserva() {
        // required by Hibernate Reactive
    }

    private Reserva(UUID id, UUID clienteId, UUID profesionalId, LocalDate fecha, RangoHorario rango,
            EstadoReserva estado) {
        this.id = id;
        this.clienteId = requireNoNull(clienteId, "clienteId");
        this.profesionalId = requireNoNull(profesionalId, "profesionalId");
        this.fecha = requireNoNull(fecha, "fecha");
        RangoHorario rangoValidado = requireNoNull(rango, "rango");
        this.horaInicio = rangoValidado.inicio();
        this.horaFin = rangoValidado.fin();
        this.estado = estado;
    }

    public static Reserva crear(UUID clienteId, UUID profesionalId, LocalDate fecha, RangoHorario rango) {
        return new Reserva(UUID.randomUUID(), clienteId, profesionalId, fecha, rango, EstadoReserva.CREADA);
    }

    private static <T> T requireNoNull(T valor, String campo) {
        if (valor == null) {
            throw new CampoRequeridoException(campo);
        }
        return valor;
    }

    public void cancelar() {
        if (estado != EstadoReserva.CREADA) {
            throw new EstadoReservaInvalidoException(estado);
        }
        this.estado = EstadoReserva.CANCELADA;
    }

    public boolean estaActiva() {
        return estado == EstadoReserva.CREADA;
    }

    public UUID getId() {
        return id;
    }

    public UUID getClienteId() {
        return clienteId;
    }

    public UUID getProfesionalId() {
        return profesionalId;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public RangoHorario getRango() {
        return new RangoHorario(horaInicio, horaFin);
    }

    public EstadoReserva getEstado() {
        return estado;
    }
}
