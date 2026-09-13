package com.mitocode.reserva.application;

import com.mitocode.cliente.domain.Cliente;
import com.mitocode.cliente.domain.ClienteRepository;
import com.mitocode.horario.domain.HorarioDisponibleRepository;
import com.mitocode.profesional.domain.Profesional;
import com.mitocode.profesional.domain.ProfesionalRepository;
import com.mitocode.reserva.domain.Reserva;
import com.mitocode.reserva.domain.ReservaRepository;
import com.mitocode.reserva.domain.ValidadorCreacionReserva;
import com.mitocode.shared.domain.RangoHorario;
import com.mitocode.shared.exception.EntidadInactivaException;
import com.mitocode.shared.exception.RecursoNoEncontradoException;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ReservaService {

    @Inject
    ReservaRepository reservaRepository;

    @Inject
    HorarioDisponibleRepository horarioRepository;

    @Inject
    ClienteRepository clienteRepository;

    @Inject
    ProfesionalRepository profesionalRepository;

    public Uni<Reserva> crear(UUID clienteId, UUID profesionalId, LocalDate fecha, LocalTime horaInicio,
            LocalTime horaFin) {
        RangoHorario rango = new RangoHorario(horaInicio, horaFin);
        Reserva nueva = Reserva.crear(clienteId, profesionalId, fecha, rango);

        return clienteRepository.buscarPorId(clienteId)
                .onItem().ifNull().failWith(() -> new RecursoNoEncontradoException("Cliente", clienteId))
                .chain(cliente -> profesionalRepository.buscarPorId(profesionalId)
                        .onItem().ifNull().failWith(() -> new RecursoNoEncontradoException("Profesional", profesionalId))
                        .invoke(profesional -> validarActivos(cliente, profesional)))
                .chain(profesional -> horarioRepository.buscarPorProfesionalYFecha(profesionalId, fecha)
                        .chain(horarios -> reservaRepository.buscarPorProfesionalYFecha(profesionalId, fecha)
                                .invoke(reservas -> ValidadorCreacionReserva.validar(nueva, horarios, reservas))))
                .chain(reservas -> reservaRepository.guardar(nueva));
    }

    public Uni<Reserva> cancelar(UUID id) {
        return reservaRepository.buscarPorId(id)
                .onItem().ifNull().failWith(() -> new RecursoNoEncontradoException("Reserva", id))
                .invoke(Reserva::cancelar)
                .chain(reservaRepository::actualizar);
    }

    public Uni<Map<LocalDate, List<Reserva>>> agruparPorFecha() {
        return reservaRepository.buscarTodas()
                .map(reservas -> reservas.stream().collect(Collectors.groupingBy(Reserva::getFecha)));
    }

    private static void validarActivos(Cliente cliente, Profesional profesional) {
        if (!cliente.isEstadoActivo()) {
            throw new EntidadInactivaException("Cliente", cliente.getId());
        }
        if (!profesional.isEstadoActivo()) {
            throw new EntidadInactivaException("Profesional", profesional.getId());
        }
    }
}
