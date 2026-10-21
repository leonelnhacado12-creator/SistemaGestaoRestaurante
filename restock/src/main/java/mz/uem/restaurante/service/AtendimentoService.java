package mz.uem.restaurante.service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import mz.uem.restaurante.dao.AtendimentoDAO;
import mz.uem.restaurante.model.MensagemAtendimento;
import mz.uem.restaurante.model.RemetenteAtendimento;
import mz.uem.restaurante.model.TipoAtendimento;
import mz.uem.restaurante.validation.Validador;

public class AtendimentoService {
    private final List<MensagemAtendimento> mensagens = new ArrayList<>();
    private AtendimentoDAO dao;
    private long proximoId = 1;

    public void activarBaseDados(AtendimentoDAO novoDao) throws SQLException {
        dao = novoDao;
        mensagens.clear(); mensagens.addAll(dao.listar());
        proximoId = mensagens.stream().mapToLong(MensagemAtendimento::getId).max().orElse(0) + 1;
    }

    public MensagemAtendimento enviarCliente(String cliente, String telefone, TipoAtendimento tipo, String texto) {
        return enviar(cliente, telefone, tipo, RemetenteAtendimento.CLIENTE, texto);
    }

    public MensagemAtendimento responder(MensagemAtendimento contexto, String texto) {
        return enviar(contexto.getCliente(), contexto.getTelefone(), contexto.getTipo(), RemetenteAtendimento.ATENDENTE, texto);
    }

    public List<MensagemAtendimento> listar() {
        return mensagens.stream().sorted(Comparator.comparing(MensagemAtendimento::getDataHora).reversed()).toList();
    }

    public List<MensagemAtendimento> conversa(String cliente, String telefone) {
        String nome = cliente == null ? "" : cliente.trim();
        String contacto = telefone == null ? "" : telefone.trim();
        return mensagens.stream()
                .filter(m -> m.getCliente().equalsIgnoreCase(nome) && (contacto.isBlank() || m.getTelefone().equals(contacto)))
                .sorted(Comparator.comparing(MensagemAtendimento::getDataHora))
                .toList();
    }

    private MensagemAtendimento enviar(String cliente, String telefone, TipoAtendimento tipo, RemetenteAtendimento remetente, String texto) {
        cliente = Validador.obrigatorio(cliente, "Nome do cliente");
        texto = Validador.obrigatorio(texto, "Mensagem");
        telefone = telefone == null ? "" : telefone.trim();
        if (!telefone.isBlank()) telefone = Validador.telefoneMocambicano(telefone);
        MensagemAtendimento mensagem = new MensagemAtendimento(proximoId++, cliente, telefone,
                tipo == null ? TipoAtendimento.OUTRO : tipo, remetente, texto, LocalDateTime.now());
        try {
            if (dao != null) dao.guardar(mensagem);
        } catch (SQLException ex) {
            throw new IllegalStateException("Não foi possível enviar a mensagem: " + ex.getMessage(), ex);
        }
        mensagens.add(mensagem);
        return mensagem;
    }
}
