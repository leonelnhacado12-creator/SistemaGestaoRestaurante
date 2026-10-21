package mz.uem.restaurante.controller;

import mz.uem.restaurante.security.PerfilAcesso;
import mz.uem.restaurante.security.UsuarioSistema;
import mz.uem.restaurante.service.DadosSistema;

/** Ponto único entre as telas e os serviços: mantém a sessão e as permissões. */
public class AplicacaoController {
    private final DadosSistema dados = new DadosSistema();
    private UsuarioSistema sessao;

    public DadosSistema getDados() { return dados; }
    public UsuarioSistema getSessao() { return sessao; }

    public boolean autenticar(String username, String senha) {
        sessao = dados.autenticar(username, senha);
        return sessao != null;
    }

    public void terminarSessao() { sessao = null; }

    public boolean temPerfil(PerfilAcesso perfil) {
        return sessao != null && sessao.getPerfil() == perfil;
    }

    public boolean podeGerirCatalogo() {
        return sessao != null && sessao.getPerfil() != PerfilAcesso.ATENDENTE;
    }
}
