package com.mitocode.cliente.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mitocode.cliente.domain.Cliente;
import com.mitocode.cliente.domain.ClienteRepository;
import com.mitocode.cliente.domain.EmailInvalidoException;
import com.mitocode.shared.exception.RecursoNoEncontradoException;
import io.smallrye.mutiny.Uni;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ClienteServiceTest {

    private final ClienteRepositoryFake repository = new ClienteRepositoryFake();
    private final ClienteService service = new ClienteService();

    @BeforeEach
    void setUp() {
        service.repository = repository;
    }

    @Test
    void actualizarConDatosValidosActualizaYPersisteLosCambios() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");
        repository.agregar(cliente);

        Cliente actualizado = service
                .actualizar(cliente.getId(), "Maria", "Gomez", "maria.gomez@mail.com", "111222333")
                .await().indefinitely();

        assertThat(actualizado.getNombres()).isEqualTo("Maria");
        assertThat(actualizado.getApellidos()).isEqualTo("Gomez");
        assertThat(actualizado.getEmail()).isEqualTo("maria.gomez@mail.com");
        assertThat(actualizado.getTelefono()).isEqualTo("111222333");
        assertThat(repository.actualizaciones).contains(cliente.getId());
    }

    @Test
    void actualizarConIdInexistenteLanzaRecursoNoEncontradoException() {
        Uni<Cliente> uni = service.actualizar(UUID.randomUUID(), "Maria", "Gomez", "maria.gomez@mail.com",
                "111222333");

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    void actualizarConEmailInvalidoLanzaEmailInvalidoException() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");
        repository.agregar(cliente);

        Uni<Cliente> uni = service.actualizar(cliente.getId(), "Maria", "Gomez", "no-es-un-email", "111222333");

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(EmailInvalidoException.class);
    }

    @Test
    void eliminarDesactivaYPersisteElCliente() {
        Cliente cliente = Cliente.crear("Ana", "Torres", "ana.torres@mail.com", "999888777");
        repository.agregar(cliente);

        service.eliminar(cliente.getId()).await().indefinitely();

        assertThat(cliente.isEstadoActivo()).isFalse();
        assertThat(repository.actualizaciones).contains(cliente.getId());
    }

    @Test
    void eliminarConIdInexistenteLanzaRecursoNoEncontradoException() {
        Uni<Void> uni = service.eliminar(UUID.randomUUID());

        assertThatThrownBy(() -> uni.await().indefinitely())
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    private static class ClienteRepositoryFake implements ClienteRepository {
        private final Map<UUID, Cliente> datos = new HashMap<>();
        private final List<UUID> actualizaciones = new ArrayList<>();

        void agregar(Cliente cliente) {
            datos.put(cliente.getId(), cliente);
        }

        @Override
        public Uni<Cliente> guardar(Cliente cliente) {
            agregar(cliente);
            return Uni.createFrom().item(cliente);
        }

        @Override
        public Uni<Cliente> buscarPorId(UUID id) {
            return Uni.createFrom().item(datos.get(id));
        }

        @Override
        public Uni<Cliente> actualizar(Cliente cliente) {
            actualizaciones.add(cliente.getId());
            datos.put(cliente.getId(), cliente);
            return Uni.createFrom().item(cliente);
        }
    }
}
