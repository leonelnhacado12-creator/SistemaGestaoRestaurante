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
import mz.uem.restaurante.model.NotificacaoCliente;

public class NotificacaoDAO {
    public NotificacaoCliente guardar(NotificacaoCliente notificacao) throws SQLException {
        String sql = "INSERT INTO notificacoes_cliente(pedido_id,cliente,telefone,mensagem,data_hora,lida) VALUES(?,?,?,?,?,?)";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notificacao.getPedidoId());
            ps.setString(2, notificacao.getCliente());
            ps.setString(3, notificacao.getTelefone());
            ps.setString(4, notificacao.getMensagem());
            ps.setTimestamp(5, Timestamp.valueOf(notificacao.getDataHora()));
            ps.setBoolean(6, notificacao.isLida());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) notificacao.setId(rs.getLong(1)); }
        }
        return notificacao;
    }

    public List<NotificacaoCliente> listar() throws SQLException {
        List<NotificacaoCliente> resultado = new ArrayList<>();
        String sql = "SELECT id,pedido_id,cliente,COALESCE(telefone,''),mensagem,data_hora,lida FROM notificacoes_cliente ORDER BY data_hora DESC,id DESC";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) resultado.add(mapear(rs));
        }
        return resultado;
    }

    private NotificacaoCliente mapear(ResultSet rs) throws SQLException {
        Timestamp data = rs.getTimestamp("data_hora");
        return new NotificacaoCliente(rs.getLong("id"), rs.getInt("pedido_id"), rs.getString("cliente"),
                rs.getString(4), rs.getString("mensagem"), data == null ? LocalDateTime.now() : data.toLocalDateTime(), rs.getBoolean("lida"));
    }
}
