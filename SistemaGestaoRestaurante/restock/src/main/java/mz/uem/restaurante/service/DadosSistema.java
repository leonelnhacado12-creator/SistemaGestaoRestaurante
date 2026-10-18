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
import java.sql.SQLException;

public class DadosSistema {
    public final ProdutoService produtoService = new ProdutoService();
    public final PedidoService pedidoService = new PedidoService();
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
            authService.activarBaseDados(new UsuarioDAO());
            pedidoService.activarBaseDados(new PedidoDAO());
            baseDadosActiva = true;
        } catch (SQLException | RuntimeException ex) {
            baseDadosActiva = false;
            erroBaseDados = ex.getMessage();
            System.err.println("Não foi possível activar a base de dados. Verifique MySQL e RESTAURANTE_DB_URL. Motivo: " + erroBaseDados);
        }
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

    public mz.uem.restaurante.security.UsuarioSistema autenticar(String username, String senha) {
        return authService.autenticar(username, senha);
    }
}
