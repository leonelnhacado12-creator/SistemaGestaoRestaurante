package mz.uem.restaurante.validation;

import mz.uem.restaurante.exception.ValidacaoException;
import mz.uem.restaurante.model.OperadoraCarteira;

/** Regras de validação reutilizadas pelos serviços e pelas telas. */
public final class Validador {
    private Validador() { }

    public static String obrigatorio(String valor, String campo) {
        String limpo = valor == null ? "" : valor.trim();
        if (limpo.isEmpty()) {
            throw new ValidacaoException("O campo \"" + campo + "\" é obrigatório.");
        }
        return limpo;
    }

    public static double positivo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor <= 0) {
            throw new ValidacaoException("O campo \"" + campo + "\" deve ser maior que zero.");
        }
        return valor;
    }

    public static double naoNegativo(double valor, String campo) {
        if (!Double.isFinite(valor) || valor < 0) {
            throw new ValidacaoException("O campo \"" + campo + "\" não pode ser negativo.");
        }
        return valor;
    }

    public static double percentual(double valor) {
        if (!Double.isFinite(valor) || valor < 0 || valor > 100) {
            throw new ValidacaoException("O desconto deve estar entre 0 e 100%.");
        }
        return valor;
    }

    public static String username(String valor) {
        String limpo = obrigatorio(valor, "Utilizador");
        if (!limpo.matches("[a-zA-Z0-9._-]{3,40}")) {
            throw new ValidacaoException("O utilizador deve ter 3 a 40 caracteres: letras, números, ponto, hífen ou sublinhado.");
        }
        return limpo;
    }

    public static String senha(String valor) {
        if (valor == null || valor.length() < 4) {
            throw new ValidacaoException("A senha deve ter pelo menos 4 caracteres.");
        }
        return valor;
    }

    /** Normaliza +258 84 123 4567 para o formato nacional 841234567. */
    public static String telefoneMocambicano(String valor) {
        String telefone = obrigatorio(valor, "Telefone").replaceAll("[\\s()-]", "");
        if (telefone.startsWith("+258")) telefone = telefone.substring(4);
        else if (telefone.startsWith("258") && telefone.length() == 12) telefone = telefone.substring(3);
        if (!telefone.matches("8[0-9]{8}")) {
            throw new ValidacaoException("Introduza um número moçambicano válido com 9 dígitos (ex.: 841234567).");
        }
        return telefone;
    }

    /** Garante que a carteira móvel corresponde à rede indicada pelo seu prefixo. */
    public static String telefoneCarteira(String valor, OperadoraCarteira operadora) {
        String telefone = telefoneMocambicano(valor);
        if (operadora == null || !operadora.aceita(telefone)) {
            String carteira = operadora == null ? "carteira móvel" : operadora.getDescricao();
            String prefixos = operadora == null ? "" : switch (operadora) {
                case EMOLA -> "86 ou 87";
                case MPESA -> "84 ou 85";
                case MKESH -> "82 ou 83";
            };
            throw new ValidacaoException("O número não pertence à rede " + carteira + ". Use um número iniciado por " + prefixos + ".");
        }
        return telefone;
    }
}
