package com.mitocode.cliente.infrastructure;

import com.mitocode.cliente.domain.Cliente;
import java.util.UUID;

public record ClienteResponse(
        UUID id,
        String nombres,
        String apellidos,
        String email,
        String telefono,
        boolean estadoActivo) {

    public static ClienteResponse from(Cliente cliente) {
        return new ClienteResponse(
                cliente.getId(),
                cliente.getNombres(),
                cliente.getApellidos(),
                cliente.getEmail(),
                cliente.getTelefono(),
                cliente.isEstadoActivo());
    }
}
