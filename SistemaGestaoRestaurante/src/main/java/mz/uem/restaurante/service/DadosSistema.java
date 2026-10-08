package mz.uem.restaurante.service;

import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.model.*;

public class DadosSistema {
    public final ProdutoService produtoService = new ProdutoService();
    public final PedidoService pedidoService = new PedidoService();
    public final PromocaoService promocaoService = new PromocaoService();
    public final Restaurante restaurante = new Restaurante("Restaurante UEM", "08:00 - 22:00", true);
    public final List<Usuario> usuarios = new ArrayList<>();

    public DadosSistema() {
        usuarios.add(new Gerente(1, "Administrador", "admin", "1234", "Gerente Geral"));
    }

    public Usuario autenticar(String username, String senha) {
        return usuarios.stream()
                .filter(u -> u.getUsername().equals(username) && u.getSenha().equals(senha))
                .findFirst().orElse(null);
    }
}
