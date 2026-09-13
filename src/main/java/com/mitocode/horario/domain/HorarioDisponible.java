package com.mitocode.horario.domain;

import com.mitocode.shared.domain.RangoHorario;
import com.mitocode.shared.exception.CampoRequeridoException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "horario_disponible")
public class HorarioDisponible {

    @Id
    private UUID id;

    @Column(name = "profesional_id")
    private UUID profesionalId;

    private LocalDate fecha;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;

    private boolean estado;

    protected HorarioDisponible() {
        // required by Hibernate Reactive
    }

    private HorarioDisponible(UUID id, UUID profesionalId, LocalDate fecha, RangoHorario rango, boolean estado) {
        this.id = id;
        this.profesionalId = requireNoNull(profesionalId, "profesionalId");
        this.fecha = requireNoNull(fecha, "fecha");
        RangoHorario rangoValidado = requireNoNull(rango, "rango");
        this.horaInicio = rangoValidado.inicio();
        this.horaFin = rangoValidado.fin();
        this.estado = estado;
    }

    public static HorarioDisponible crear(UUID profesionalId, LocalDate fecha, RangoHorario rango) {
        return new HorarioDisponible(UUID.randomUUID(), profesionalId, fecha, rango, true);
    }

    private static <T> T requireNoNull(T valor, String campo) {
        if (valor == null) {
            throw new CampoRequeridoException(campo);
        }
        return valor;
    }

    public void deshabilitar() {
        this.estado = false;
    }

    public void habilitar() {
        this.estado = true;
    }

    public UUID getId() {
        return id;
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

    public boolean isEstado() {
        return estado;
    }
}
