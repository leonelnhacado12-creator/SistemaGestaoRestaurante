package mz.uem.restaurante.model;

import java.util.Set;

/** Operadoras de carteira móvel suportadas e respectivos prefixos nacionais. */
public enum OperadoraCarteira {
    EMOLA("e-Mola", Set.of("86", "87")),
    MPESA("M-Pesa", Set.of("84", "85")),
    MKESH("mKesh", Set.of("82", "83"));

    private final String descricao;
    private final Set<String> prefixos;

    OperadoraCarteira(String descricao, Set<String> prefixos) {
        this.descricao = descricao;
        this.prefixos = prefixos;
    }

    public String getDescricao() { return descricao; }

    public boolean aceita(String telefoneNacional) {
        return telefoneNacional != null && telefoneNacional.length() == 9
                && prefixos.contains(telefoneNacional.substring(0, 2));
    }

    public static OperadoraCarteira porDescricao(String descricao) {
        for (OperadoraCarteira operadora : values()) {
            if (operadora.descricao.equalsIgnoreCase(descricao)) return operadora;
        }
        throw new IllegalArgumentException("Carteira móvel desconhecida: " + descricao);
    }
}
