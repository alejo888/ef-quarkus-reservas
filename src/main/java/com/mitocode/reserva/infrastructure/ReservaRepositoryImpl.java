package com.mitocode.reserva.infrastructure;

import com.mitocode.reserva.domain.Reserva;
import com.mitocode.reserva.domain.ReservaRepository;
import com.mitocode.reserva.domain.ReservaSolapadaException;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.hibernate.exception.ConstraintViolationException;

@ApplicationScoped
public class ReservaRepositoryImpl implements ReservaRepository, PanacheRepositoryBase<Reserva, UUID> {

    private static final String EXCLUSION_VIOLATION_SQLSTATE = "23P01";

    @Override
    public Uni<Reserva> guardar(Reserva reserva) {
        return Panache.withTransaction(() -> persist(reserva))
                .replaceWith(reserva)
                .onFailure(ReservaRepositoryImpl::esViolacionDeSolapamiento)
                .transform(failure -> new ReservaSolapadaException(reserva.getProfesionalId(), reserva.getFecha()));
    }

    private static boolean esViolacionDeSolapamiento(Throwable failure) {
        for (Throwable actual = failure; actual != null; actual = actual.getCause()) {
            if (actual instanceof ConstraintViolationException constraintViolation
                    && EXCLUSION_VIOLATION_SQLSTATE.equals(constraintViolation.getSQLState())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Uni<Reserva> actualizar(Reserva reserva) {
        return Panache.withTransaction(() -> Panache.getSession().chain(session -> session.merge(reserva)));
    }

    @Override
    public Uni<List<Reserva>> buscarPorProfesionalYFecha(UUID profesionalId, LocalDate fecha) {
        return list("profesionalId = ?1 and fecha = ?2", profesionalId, fecha);
    }

    @Override
    public Uni<Reserva> buscarPorId(UUID id) {
        return findById(id);
    }

    @Override
    public Uni<List<Reserva>> buscarTodas() {
        return listAll();
    }
}
