package mz.uem.restaurante.security;

/** Perfis funcionais da aplicação. As permissões são verificadas pela interface. */
public enum PerfilAcesso {
    ADMINISTRADOR("Administrador"),
    GERENTE("Gerente"),
    ATENDENTE("Atendente");

    private final String descricao;

    PerfilAcesso(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
