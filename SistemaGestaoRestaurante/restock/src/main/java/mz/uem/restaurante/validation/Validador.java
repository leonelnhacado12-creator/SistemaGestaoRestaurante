package mz.uem.restaurante.validation;

import mz.uem.restaurante.exception.ValidacaoException;

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
}
