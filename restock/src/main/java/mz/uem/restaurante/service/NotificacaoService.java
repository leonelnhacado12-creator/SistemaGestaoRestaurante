package mz.uem.restaurante.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mz.uem.restaurante.dao.NotificacaoDAO;
import mz.uem.restaurante.model.NotificacaoCliente;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.StatusPedido;
import mz.uem.restaurante.model.EstadoPagamento;

/** Regista notificações consultáveis no sistema; o envio por SMS é intencionalmente desacoplado. */
public class NotificacaoService {
    private final List<NotificacaoCliente> notificacoes = new ArrayList<>();
    private NotificacaoDAO dao;
    private long proximoId = 1;

    public void activarBaseDados(NotificacaoDAO novoDao) throws SQLException {
        dao = novoDao;
        notificacoes.clear();
        notificacoes.addAll(dao.listar());
        proximoId = notificacoes.stream().mapToLong(NotificacaoCliente::getId).max().orElse(0) + 1;
    }

    public void aoAlterarPedido(Pedido pedido, StatusPedido estadoAnterior) {
        String mensagem = switch (pedido.getStatus()) {
            case PENDENTE -> "O pedido #" + pedido.getId() + " foi recebido e aguarda preparação.";
            case EM_PREPARACAO -> "O pedido #" + pedido.getId() + " está em preparação.";
            case PRONTO -> "O pedido #" + pedido.getId() + " está pronto para levantamento/entrega.";
            case ENTREGUE -> "O pedido #" + pedido.getId() + " foi marcado como entregue. Obrigado pela preferência.";
        };
        criar(pedido.getId(), pedido.getCliente().getNome(), pedido.getCliente().getTelefone(), mensagem);
    }

    public void aoAlterarPagamento(Pedido pedido, EstadoPagamento estadoAnterior) {
        String mensagem = switch (pedido.getEstadoPagamento()) {
            case EM_VALIDACAO -> "Recebemos o registo de pagamento do pedido #" + pedido.getId() + ". Aguarde a confirmação da equipa.";
            case CONFIRMADO -> "O pagamento do pedido #" + pedido.getId() + " foi confirmado. A cozinha pode iniciar a preparação.";
            case RECUSADO -> "O pagamento do pedido #" + pedido.getId() + " não foi confirmado. Contacte o atendente ou tente novamente.";
        };
        criar(pedido.getId(), pedido.getCliente().getNome(), pedido.getCliente().getTelefone(), mensagem);
    }

    public void notificarReserva(mz.uem.restaurante.model.Reserva reserva, mz.uem.restaurante.model.EstadoReserva anterior) {
        String mensagem = "Reserva #" + reserva.getId() + " para a mesa " + reserva.getMesa().getNumero() + ": "
                + switch (reserva.getEstado()) {
                    case PENDENTE -> "recebida e em análise.";
                    case CONFIRMADA -> "confirmada.";
                    case CANCELADA -> "cancelada.";
                    case CONCLUIDA -> "concluída. Obrigado pela visita.";
                    case NAO_COMPARECEU -> "marcada como não comparecida.";
                };
        criar(0, reserva.getCliente(), reserva.getTelefone(), mensagem);
    }

    public void notificarLembreteReserva(mz.uem.restaurante.model.Reserva reserva) {
        criar(0, reserva.getCliente(), reserva.getTelefone(), "Lembrete: a sua reserva #" + reserva.getId() + " para a mesa " + reserva.getMesa().getNumero() + " é em " + reserva.getDataHora().toString().replace('T', ' ') + ".");
    }

    public List<NotificacaoCliente> listarParaCliente(String identificador) {
        String procura = identificador == null ? "" : identificador.trim();
        if (procura.isEmpty()) return List.of();
        String normalizado = procura.replaceAll("[\\s()-]", "");
        if (normalizado.startsWith("+258")) normalizado = normalizado.substring(4);
        final String telefone = normalizado;
        return notificacoes.stream()
                .filter(n -> n.getCliente().equalsIgnoreCase(procura) || (!telefone.isBlank() && n.getTelefone().equals(telefone)))
                .sorted(Comparator.comparing(NotificacaoCliente::getDataHora).reversed())
                .toList();
    }

    private void criar(int pedidoId, String cliente, String telefone, String mensagem) {
        NotificacaoCliente notificacao = new NotificacaoCliente(proximoId++, pedidoId, cliente, telefone, mensagem, LocalDateTime.now(), false);
        try {
            if (dao != null) dao.guardar(notificacao);
        } catch (SQLException ex) {
            throw new IllegalStateException("Não foi possível guardar a notificação: " + ex.getMessage(), ex);
        }
        notificacoes.add(notificacao);
    }
}
