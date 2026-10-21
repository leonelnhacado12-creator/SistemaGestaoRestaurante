package mz.uem.restaurante.model;

import java.time.LocalDateTime;

public class RegistoAuditoria {
    private final long id; private final String utilizador; private final String operacao; private final String entidade; private final String detalhe; private final LocalDateTime dataHora;
    public RegistoAuditoria(long id,String utilizador,String operacao,String entidade,String detalhe,LocalDateTime dataHora){this.id=id;this.utilizador=utilizador;this.operacao=operacao;this.entidade=entidade;this.detalhe=detalhe;this.dataHora=dataHora;}
    public long getId(){return id;} public String getUtilizador(){return utilizador;} public String getOperacao(){return operacao;} public String getEntidade(){return entidade;} public String getDetalhe(){return detalhe;} public LocalDateTime getDataHora(){return dataHora;}
}
