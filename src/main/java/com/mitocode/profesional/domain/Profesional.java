package com.mitocode.profesional.domain;

import com.mitocode.shared.exception.CampoRequeridoException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "profesional")
public class Profesional {

    @Id
    private UUID id;

    private String nombres;

    private String apellidos;

    private String especialidad;

    @Column(name = "estado_activo")
    private boolean estadoActivo;

    protected Profesional() {
        // required by Hibernate Reactive
    }

    private Profesional(UUID id, String nombres, String apellidos, String especialidad, boolean estadoActivo) {
        this.id = id;
        this.nombres = requireNoBlank(nombres, "nombres");
        this.apellidos = requireNoBlank(apellidos, "apellidos");
        this.especialidad = requireNoBlank(especialidad, "especialidad");
        this.estadoActivo = estadoActivo;
    }

    public static Profesional crear(String nombres, String apellidos, String especialidad) {
        return new Profesional(UUID.randomUUID(), nombres, apellidos, especialidad, true);
    }

    private static String requireNoBlank(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new CampoRequeridoException(campo);
        }
        return valor;
    }

    public void actualizarDatos(String nombres, String apellidos, String especialidad) {
        this.nombres = requireNoBlank(nombres, "nombres");
        this.apellidos = requireNoBlank(apellidos, "apellidos");
        this.especialidad = requireNoBlank(especialidad, "especialidad");
    }

    public void activar() {
        this.estadoActivo = true;
    }

    public void desactivar() {
        this.estadoActivo = false;
    }

    public UUID getId() {
        return id;
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getEspecialidad() {
        return especialidad;
    }

    public boolean isEstadoActivo() {
        return estadoActivo;
    }
}
