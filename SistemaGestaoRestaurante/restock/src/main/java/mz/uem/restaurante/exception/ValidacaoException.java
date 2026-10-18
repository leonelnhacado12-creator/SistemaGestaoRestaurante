package mz.uem.restaurante.exception;

/** Erro de regra de negócio apresentado à interface de forma amigável. */
public class ValidacaoException extends IllegalArgumentException {
    public ValidacaoException(String mensagem) {
        super(mensagem);
    }
}
