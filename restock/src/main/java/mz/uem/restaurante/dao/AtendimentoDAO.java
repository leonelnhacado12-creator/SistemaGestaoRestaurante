package mz.uem.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.MensagemAtendimento;
import mz.uem.restaurante.model.RemetenteAtendimento;
import mz.uem.restaurante.model.TipoAtendimento;

public class AtendimentoDAO {
    public MensagemAtendimento guardar(MensagemAtendimento mensagem) throws SQLException {
        String sql = "INSERT INTO mensagens_atendimento(cliente,telefone,tipo,remetente,mensagem,data_hora) VALUES(?,?,?,?,?,?)";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mensagem.getCliente()); ps.setString(2, mensagem.getTelefone());
            ps.setString(3, mensagem.getTipo().name()); ps.setString(4, mensagem.getRemetente().name());
            ps.setString(5, mensagem.getMensagem()); ps.setTimestamp(6, Timestamp.valueOf(mensagem.getDataHora()));
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) mensagem.setId(rs.getLong(1)); }
        }
        return mensagem;
    }

    public List<MensagemAtendimento> listar() throws SQLException {
        List<MensagemAtendimento> resultado = new ArrayList<>();
        String sql = "SELECT id,cliente,COALESCE(telefone,''),tipo,remetente,mensagem,data_hora FROM mensagens_atendimento ORDER BY data_hora ASC,id ASC";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
        }
        return resultado;
    }

    private MensagemAtendimento mapear(ResultSet rs) throws SQLException {
        Timestamp data = rs.getTimestamp("data_hora");
        return new MensagemAtendimento(rs.getLong("id"), rs.getString("cliente"), rs.getString(3),
                TipoAtendimento.valueOf(rs.getString("tipo")), RemetenteAtendimento.valueOf(rs.getString("remetente")),
                rs.getString("mensagem"), data == null ? LocalDateTime.now() : data.toLocalDateTime());
    }
}
