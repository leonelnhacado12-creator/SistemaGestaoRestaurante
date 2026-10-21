package mz.uem.restaurante.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.dao.MesaDAO;
import mz.uem.restaurante.dao.ReservaDAO;
import mz.uem.restaurante.model.EstadoMesa;
import mz.uem.restaurante.model.EstadoReserva;
import mz.uem.restaurante.model.Mesa;
import mz.uem.restaurante.model.Reserva;
import mz.uem.restaurante.validation.Validador;

/** Reserva mesas por blocos de duas horas e impede reservas simultâneas na mesma mesa. */
public class ReservaService {
    private static final int DURACAO_RESERVA_HORAS = 2;
    private final List<Mesa> mesas = new ArrayList<>();
    private final List<Reserva> reservas = new ArrayList<>();
    private final List<ReservaStatusListener> ouvintes = new ArrayList<>();
    private MesaDAO mesaDao;
    private ReservaDAO reservaDao;
    private int proximaMesaId = 1;
    private int proximaReservaId = 1;

    public void activarBaseDados(MesaDAO novoMesaDao, ReservaDAO novoReservaDao) throws SQLException {
        mesaDao = novoMesaDao; reservaDao = novoReservaDao;
        mesas.clear(); mesas.addAll(mesaDao.listar());
        reservas.clear(); reservas.addAll(reservaDao.listar());
        for (Reserva reserva : reservas) {
            Mesa mesa = buscarMesa(reserva.getMesa().getId());
            if (mesa != null) reserva.setMesa(mesa);
        }
        proximaMesaId = mesas.stream().mapToInt(Mesa::getId).max().orElse(0) + 1;
        proximaReservaId = reservas.stream().mapToInt(Reserva::getId).max().orElse(0) + 1;
    }

    public void adicionarOuvinte(ReservaStatusListener ouvinte) { if (ouvinte != null) ouvintes.add(ouvinte); }
    public List<Mesa> listarMesas() { return new ArrayList<>(mesas); }
    public List<Reserva> listarReservas() { return reservas.stream().sorted((a, b) -> b.getDataHora().compareTo(a.getDataHora())).toList(); }
    /** Mesas com lotação suficiente e sem outra reserva no mesmo bloco de duas horas. */
    public List<Mesa> listarMesasDisponiveis(LocalDateTime dataHora, int pessoas) {
        if (dataHora == null || !dataHora.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("Seleccione uma data e hora futuras.");
        if (pessoas <= 0) throw new IllegalArgumentException("Indique pelo menos uma pessoa.");
        LocalDateTime fim = dataHora.plusHours(DURACAO_RESERVA_HORAS);
        return mesas.stream().filter(m -> m.getLugares() >= pessoas).filter(m -> !temConflito(m.getId(), dataHora, fim, 0)).toList();
    }
    public List<Reserva> listarReservasCliente(String telefone) {
        String contacto = telefone == null ? "" : telefone.trim();
        return reservas.stream().filter(r -> !contacto.isBlank() && contacto.equals(r.getTelefone())).sorted((a, b) -> a.getDataHora().compareTo(b.getDataHora())).toList();
    }
    public Mesa buscarMesa(int id) { return mesas.stream().filter(m -> m.getId() == id).findFirst().orElse(null); }
    public Reserva buscarReserva(int id) { return reservas.stream().filter(r -> r.getId() == id).findFirst().orElse(null); }

    public Mesa guardarMesa(int numero, int lugares) {
        if (numero <= 0) throw new IllegalArgumentException("O número da mesa deve ser maior que zero.");
        if (lugares <= 0) throw new IllegalArgumentException("A mesa deve ter pelo menos um lugar.");
        if (mesas.stream().anyMatch(m -> m.getNumero() == numero)) throw new IllegalArgumentException("Já existe uma mesa com o número " + numero + ".");
        Mesa mesa = new Mesa(0, numero, lugares, EstadoMesa.LIVRE);
        try {
            if (mesaDao != null) mesaDao.guardar(mesa);
            else mesa.setId(proximaMesaId++);
        }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível guardar a mesa: " + ex.getMessage(), ex); }
        mesas.add(mesa);
        return mesa;
    }

    public void atualizarEstadoMesa(int mesaId, EstadoMesa estado) {
        Mesa mesa = exigirMesa(mesaId);
        mesa.setEstado(estado);
        try { if (mesaDao != null) mesaDao.atualizarEstado(mesaId, estado); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível actualizar a mesa: " + ex.getMessage(), ex); }
    }

    public Reserva criarReserva(String cliente, String telefone, int mesaId, LocalDateTime dataHora, int pessoas) {
        cliente = Validador.obrigatorio(cliente, "Cliente");
        if (telefone != null && !telefone.isBlank()) telefone = Validador.telefoneMocambicano(telefone); else telefone = "";
        if (dataHora == null || !dataHora.isAfter(LocalDateTime.now())) throw new IllegalArgumentException("A reserva deve ser agendada para uma data e hora futuras.");
        if (pessoas <= 0) throw new IllegalArgumentException("Indique pelo menos uma pessoa para a reserva.");
        Mesa mesa = exigirMesa(mesaId);
        if (pessoas > mesa.getLugares()) throw new IllegalArgumentException("A mesa " + mesa.getNumero() + " tem capacidade para " + mesa.getLugares() + " pessoas.");
        if (temConflito(mesaId, dataHora, dataHora.plusHours(DURACAO_RESERVA_HORAS), 0)) throw new IllegalArgumentException("A mesa já possui uma reserva nesse intervalo de duas horas.");
        Reserva reserva = new Reserva(proximaReservaId++, mesa, cliente, telefone, dataHora, pessoas, EstadoReserva.PENDENTE);
        try { if (reservaDao != null) reservaDao.guardar(reserva); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível guardar a reserva: " + ex.getMessage(), ex); }
        reservas.add(reserva);
        atualizarEstadoMesa(mesaId, EstadoMesa.RESERVADA);
        informar(reserva, null);
        return reserva;
    }

    public void atualizarEstadoReserva(int reservaId, EstadoReserva novoEstado) {
        Reserva reserva = exigirReserva(reservaId);
        EstadoReserva anterior = reserva.getEstado();
        if (anterior == novoEstado) return;
        if ((anterior == EstadoReserva.CANCELADA || anterior == EstadoReserva.CONCLUIDA || anterior == EstadoReserva.NAO_COMPARECEU) && novoEstado != anterior) {
            throw new IllegalStateException("Uma reserva finalizada não pode voltar a ser alterada.");
        }
        if (novoEstado == EstadoReserva.NAO_COMPARECEU && reserva.getDataHora().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Só é possível marcar não compareceu depois do horário da reserva.");
        }
        reserva.setEstado(novoEstado);
        try { if (reservaDao != null) reservaDao.atualizarEstado(reservaId, novoEstado); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível actualizar a reserva: " + ex.getMessage(), ex); }
        if (novoEstado == EstadoReserva.CANCELADA || novoEstado == EstadoReserva.CONCLUIDA || novoEstado == EstadoReserva.NAO_COMPARECEU) {
            if (!temReservaActivaParaMesa(reserva.getMesa().getId(), reservaId)) atualizarEstadoMesa(reserva.getMesa().getId(), EstadoMesa.LIVRE);
        } else atualizarEstadoMesa(reserva.getMesa().getId(), EstadoMesa.RESERVADA);
        informar(reserva, anterior);
    }

    /** Permite cancelar a própria reserva até ao seu horário, usando o telefone informado no pedido. */
    public void cancelarPeloCliente(int reservaId, String telefone) {
        Reserva reserva = exigirReserva(reservaId);
        String contacto = telefone == null ? "" : telefone.trim();
        if (contacto.isBlank() || !contacto.equals(reserva.getTelefone())) throw new IllegalArgumentException("O telefone não corresponde a esta reserva.");
        if (!reserva.getDataHora().isAfter(LocalDateTime.now())) throw new IllegalStateException("A reserva já está em curso ou já passou; contacte o restaurante.");
        atualizarEstadoReserva(reservaId, EstadoReserva.CANCELADA);
    }

    public List<Reserva> listarParaLembrete(int horas) {
        LocalDateTime agora = LocalDateTime.now(); LocalDateTime limite = agora.plusHours(Math.max(1, horas));
        return reservas.stream().filter(r -> r.getEstado() == EstadoReserva.CONFIRMADA)
                .filter(r -> !r.isLembreteEnviado())
                .filter(r -> !r.getDataHora().isBefore(agora) && !r.getDataHora().isAfter(limite)).toList();
    }

    public void marcarLembreteEnviado(int reservaId) {
        Reserva reserva = exigirReserva(reservaId);
        if (reserva.isLembreteEnviado()) return;
        try { if (reservaDao != null) reservaDao.marcarLembreteEnviado(reservaId); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível registar o lembrete: " + ex.getMessage(), ex); }
        reserva.setLembreteEnviado(true);
    }

    private boolean temConflito(int mesaId, LocalDateTime inicio, LocalDateTime fim, int ignorarId) {
        if (reservaDao != null) try { return reservaDao.existeConflito(mesaId, inicio, fim, ignorarId); }
        catch (SQLException ex) { throw new IllegalStateException("Não foi possível validar disponibilidade: " + ex.getMessage(), ex); }
        return reservas.stream().filter(r -> r.getMesa().getId() == mesaId && r.getId() != ignorarId)
                .filter(r -> r.getEstado() == EstadoReserva.PENDENTE || r.getEstado() == EstadoReserva.CONFIRMADA)
                .anyMatch(r -> inicio.isBefore(r.getDataHora().plusHours(DURACAO_RESERVA_HORAS)) && fim.isAfter(r.getDataHora()));
    }

    private boolean temReservaActivaParaMesa(int mesaId, int ignorarId) {
        return reservas.stream().anyMatch(r -> r.getMesa().getId() == mesaId && r.getId() != ignorarId
                && (r.getEstado() == EstadoReserva.PENDENTE || r.getEstado() == EstadoReserva.CONFIRMADA));
    }
    private Mesa exigirMesa(int id) { Mesa mesa = buscarMesa(id); if (mesa == null) throw new IllegalArgumentException("Mesa não encontrada."); return mesa; }
    private Reserva exigirReserva(int id) { Reserva reserva = buscarReserva(id); if (reserva == null) throw new IllegalArgumentException("Reserva não encontrada."); return reserva; }
    private void informar(Reserva reserva, EstadoReserva anterior) { ouvintes.forEach(ouvinte -> ouvinte.aoAlterar(reserva, anterior)); }
}
