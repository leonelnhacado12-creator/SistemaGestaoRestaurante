package mz.uem.restaurante.service;

import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.model.*;
import mz.uem.restaurante.database.InicializadorBD;
import mz.uem.restaurante.dao.ProdutoDAO;
import mz.uem.restaurante.dao.PromocaoDAO;
import mz.uem.restaurante.dao.ClienteDAO;
import mz.uem.restaurante.dao.StockDAO;
import mz.uem.restaurante.dao.UsuarioDAO;
import mz.uem.restaurante.dao.PedidoDAO;
import mz.uem.restaurante.dao.MesaDAO;
import mz.uem.restaurante.dao.ReservaDAO;
import mz.uem.restaurante.dao.NotificacaoDAO;
import mz.uem.restaurante.dao.AtendimentoDAO;
import mz.uem.restaurante.dao.ReceitaProdutoDAO;
import java.sql.SQLException;
import java.time.LocalDate;

public class DadosSistema {
    public final ProdutoService produtoService = new ProdutoService();
    public final PedidoService pedidoService = new PedidoService();
    public final NotificacaoService notificacaoService = new NotificacaoService();
    public final AtendimentoService atendimentoService = new AtendimentoService();
    public final ReservaService reservaService = new ReservaService();
    public final ReceitaService receitaService = new ReceitaService();
    public final PromocaoService promocaoService = new PromocaoService();
    public final Restaurante restaurante = new Restaurante("Restaurante UEM", "08:00 - 22:00", true);
    public final ClienteService clienteService = new ClienteService();
    public final StockService stockService = new StockService();
    public final AuthService authService = new AuthService();
    public final AuditoriaService auditoriaService = new AuditoriaService();

    private boolean baseDadosActiva;
    private String erroBaseDados;
    public DadosSistema() {
        try {
            InicializadorBD.criarTabelas();
            produtoService.activarBaseDados(new ProdutoDAO());
            promocaoService.activarBaseDados(new PromocaoDAO());
            clienteService.activarBaseDados(new ClienteDAO());
            stockService.activarBaseDados(new StockDAO());
            receitaService.activarBaseDados(new ReceitaProdutoDAO());
            authService.activarBaseDados(new UsuarioDAO());
            pedidoService.activarBaseDados(new PedidoDAO());
            notificacaoService.activarBaseDados(new NotificacaoDAO());
            atendimentoService.activarBaseDados(new AtendimentoDAO());
            reservaService.activarBaseDados(new MesaDAO(), new ReservaDAO());
            baseDadosActiva = true;
        } catch (SQLException | RuntimeException ex) {
            baseDadosActiva = false;
            erroBaseDados = ex.getMessage();
            System.err.println("Não foi possível activar a base de dados. Verifique MySQL e RESTAURANTE_DB_URL. Motivo: " + erroBaseDados);
        }
        pedidoService.adicionarOuvinte(notificacaoService::aoAlterarPedido);
        pedidoService.adicionarOuvintePagamento(notificacaoService::aoAlterarPagamento);
        reservaService.adicionarOuvinte(notificacaoService::notificarReserva);
        pedidoService.adicionarOuvinte((pedido, anterior) -> auditoriaService.registar("sistema", "ALTERAR_ESTADO", "pedido", "Pedido #" + pedido.getId() + " -> " + pedido.getStatus()));
        pedidoService.adicionarOuvintePagamento((pedido, anterior) -> auditoriaService.registar("sistema", "ALTERAR_PAGAMENTO", "pedido", "Pedido #" + pedido.getId() + " -> " + pedido.getEstadoPagamento()));
        reservaService.adicionarOuvinte((reserva, anterior) -> auditoriaService.registar("sistema", "ALTERAR_ESTADO", "reserva", "Reserva #" + reserva.getId() + " -> " + reserva.getEstado()));
    }
    public boolean isBaseDadosActiva() { return baseDadosActiva; }
    public String getErroBaseDados() { return erroBaseDados; }
    public String estadoBaseDados() {
        return baseDadosActiva ? "Ligada" : "Indisponível: " + (erroBaseDados == null ? "sem detalhes" : erroBaseDados);
    }
    public String resumoPagamentos() {
        java.util.Map<String, Integer> contagem = new java.util.TreeMap<>();
        pedidoService.listar().forEach(p -> contagem.merge(p.getFormaPagamento().replaceAll(" \\(.*\\)$", ""), 1, Integer::sum));
        return "Pagamentos: " + (contagem.isEmpty() ? "sem registos" : contagem.toString());
    }

    public long pedidosPorTratar() {
        return pedidoService.listar().stream().filter(p -> p.getStatus() == StatusPedido.PENDENTE || p.getStatus() == StatusPedido.EM_PREPARACAO).count();
    }
    public long pedidosProntos() {
        return pedidoService.listar().stream().filter(p -> p.getStatus() == StatusPedido.PRONTO).count();
    }
    public long pagamentosPorValidar() {
        return pedidoService.listar().stream().filter(p -> p.getEstadoPagamento() == EstadoPagamento.EM_VALIDACAO).count();
    }
    public double faturacaoHoje() {
        LocalDate hoje = LocalDate.now();
        return pedidoService.listar().stream().filter(p -> p.getEstadoPagamento() == EstadoPagamento.CONFIRMADO)
                .filter(p -> hoje.equals(p.getDataHora().toLocalDate())).mapToDouble(Pedido::getTotal).sum();
    }
    public long reservasHoje() {
        LocalDate hoje = LocalDate.now();
        return reservaService.listarReservas().stream().filter(r -> hoje.equals(r.getDataHora().toLocalDate()))
                .filter(r -> r.getEstado() == EstadoReserva.PENDENTE || r.getEstado() == EstadoReserva.CONFIRMADA).count();
    }
    public String produtoMaisVendido() {
        return pedidoService.listar().stream().filter(p -> p.getEstadoPagamento() == EstadoPagamento.CONFIRMADO)
                .flatMap(p -> p.getItens().stream()).collect(java.util.stream.Collectors.groupingBy(i -> i.getProduto().getNome(), java.util.stream.Collectors.summingInt(mz.uem.restaurante.model.ItemPedido::getQuantidade)))
                .entrySet().stream().max(java.util.Map.Entry.comparingByValue()).map(java.util.Map.Entry::getKey).orElse("sem vendas");
    }

    public mz.uem.restaurante.security.UsuarioSistema autenticar(String username, String senha) {
        return authService.autenticar(username, senha);
    }
}
