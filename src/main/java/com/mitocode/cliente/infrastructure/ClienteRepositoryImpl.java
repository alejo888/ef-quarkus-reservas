package com.mitocode.cliente.infrastructure;

import com.mitocode.cliente.domain.Cliente;
import com.mitocode.cliente.domain.ClienteRepository;
import com.mitocode.cliente.domain.EmailDuplicadoException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException;

@ApplicationScoped
public class ClienteRepositoryImpl implements ClienteRepository, PanacheRepositoryBase<Cliente, UUID> {

    private static final String UNIQUE_VIOLATION_SQLSTATE = "23505";

    @Override
    public Uni<Cliente> guardar(Cliente cliente) {
        return Panache.withTransaction(() -> persist(cliente))
                .replaceWith(cliente)
                .onFailure(ClienteRepositoryImpl::esViolacionDeEmailUnico)
                .transform(failure -> new EmailDuplicadoException(cliente.getEmail()));
    }

    @Override
    public Uni<Cliente> buscarPorId(UUID id) {
        return findById(id);
    }

    private static boolean esViolacionDeEmailUnico(Throwable failure) {
        for (Throwable actual = failure; actual != null; actual = actual.getCause()) {
            if (actual instanceof ConstraintViolationException constraintViolation
                    && UNIQUE_VIOLATION_SQLSTATE.equals(constraintViolation.getSQLState())) {
                return true;
            }
        }
        return false;
    }
}
