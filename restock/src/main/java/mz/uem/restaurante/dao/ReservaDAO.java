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
import mz.uem.restaurante.model.EstadoMesa;
import mz.uem.restaurante.model.EstadoReserva;
import mz.uem.restaurante.model.Mesa;
import mz.uem.restaurante.model.Reserva;

public class ReservaDAO {
    public List<Reserva> listar() throws SQLException {
        List<Reserva> resultado = new ArrayList<>();
        String sql = "SELECT r.id,r.cliente,COALESCE(r.telefone,''),r.data_hora,r.pessoas,r.estado,r.lembrete_enviado,m.id AS mesa_id,m.numero,m.lugares,m.estado AS mesa_estado "
                + "FROM reservas r JOIN mesas m ON m.id=r.mesa_id ORDER BY r.data_hora DESC,r.id DESC";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Mesa mesa = new Mesa(rs.getInt("mesa_id"), rs.getInt("numero"), rs.getInt("lugares"), EstadoMesa.valueOf(rs.getString("mesa_estado")));
                Timestamp data = rs.getTimestamp("data_hora");
                resultado.add(new Reserva(rs.getInt("id"), mesa, rs.getString("cliente"), rs.getString(3),
                        data == null ? LocalDateTime.now() : data.toLocalDateTime(), rs.getInt("pessoas"), EstadoReserva.valueOf(rs.getString("estado")), rs.getBoolean("lembrete_enviado")));
            }
        }
        return resultado;
    }

    public Reserva guardar(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reservas(mesa_id,cliente,telefone,data_hora,pessoas,estado) VALUES(?,?,?,?,?,?)";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reserva.getMesa().getId()); ps.setString(2, reserva.getCliente()); ps.setString(3, reserva.getTelefone());
            ps.setTimestamp(4, Timestamp.valueOf(reserva.getDataHora())); ps.setInt(5, reserva.getPessoas()); ps.setString(6, reserva.getEstado().name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) reserva.setId(rs.getInt(1)); }
        }
        return reserva;
    }

    public void atualizarEstado(int reservaId, EstadoReserva estado) throws SQLException {
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement("UPDATE reservas SET estado=? WHERE id=?")) {
            ps.setString(1, estado.name()); ps.setInt(2, reservaId); ps.executeUpdate();
        }
    }

    public void marcarLembreteEnviado(int reservaId) throws SQLException {
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement("UPDATE reservas SET lembrete_enviado=TRUE WHERE id=?")) {
            ps.setInt(1, reservaId); ps.executeUpdate();
        }
    }

    public boolean existeConflito(int mesaId, LocalDateTime inicio, LocalDateTime fim, int ignorarReservaId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reservas WHERE mesa_id=? AND id<>? AND estado IN ('PENDENTE','CONFIRMADA') "
                + "AND data_hora < ? AND DATE_ADD(data_hora, INTERVAL 2 HOUR) > ?";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, mesaId); ps.setInt(2, ignorarReservaId); ps.setTimestamp(3, Timestamp.valueOf(fim)); ps.setTimestamp(4, Timestamp.valueOf(inicio));
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        }
    }
}
