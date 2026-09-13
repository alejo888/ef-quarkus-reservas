package com.mitocode.cliente.domain;

import com.mitocode.shared.exception.CampoRequeridoException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import java.util.regex.Pattern;

@Entity
@Table(name = "cliente")
public class Cliente {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Id
    private UUID id;

    private String nombres;

    private String apellidos;

    private String email;

    private String telefono;

    @Column(name = "estado_activo")
    private boolean estadoActivo;

    protected Cliente() {
        // required by Hibernate Reactive
    }

    private Cliente(UUID id, String nombres, String apellidos, String email, String telefono, boolean estadoActivo) {
        this.id = id;
        this.nombres = requireNoBlank(nombres, "nombres");
        this.apellidos = requireNoBlank(apellidos, "apellidos");
        this.email = requireValidEmail(email);
        this.telefono = requireNoBlank(telefono, "telefono");
        this.estadoActivo = estadoActivo;
    }

    public static Cliente crear(String nombres, String apellidos, String email, String telefono) {
        return new Cliente(UUID.randomUUID(), nombres, apellidos, email, telefono, true);
    }

    private static String requireNoBlank(String valor, String campo) {
        if (valor == null || valor.isBlank()) {
            throw new CampoRequeridoException(campo);
        }
        return valor;
    }

    private static String requireValidEmail(String email) {
        if (email == null || email.isBlank() || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new EmailInvalidoException(email);
        }
        return email;
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

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public boolean isEstadoActivo() {
        return estadoActivo;
    }
}
