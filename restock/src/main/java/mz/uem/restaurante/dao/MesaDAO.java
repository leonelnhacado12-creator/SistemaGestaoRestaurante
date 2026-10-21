package mz.uem.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.model.EstadoMesa;
import mz.uem.restaurante.model.Mesa;

public class MesaDAO {
    public List<Mesa> listar() throws SQLException {
        List<Mesa> resultado = new ArrayList<>();
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement("SELECT id,numero,lugares,estado FROM mesas ORDER BY numero"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) resultado.add(new Mesa(rs.getInt("id"), rs.getInt("numero"), rs.getInt("lugares"), EstadoMesa.valueOf(rs.getString("estado"))));
        }
        return resultado;
    }

    public Mesa guardar(Mesa mesa) throws SQLException {
        String sql = mesa.getId() > 0 ? "UPDATE mesas SET numero=?,lugares=?,estado=? WHERE id=?" : "INSERT INTO mesas(numero,lugares,estado) VALUES(?,?,?)";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, mesa.getNumero()); ps.setInt(2, mesa.getLugares()); ps.setString(3, mesa.getEstado().name());
            if (mesa.getId() > 0) ps.setInt(4, mesa.getId());
            ps.executeUpdate();
            if (mesa.getId() == 0) try (ResultSet rs = ps.getGeneratedKeys()) { if (rs.next()) mesa.setId(rs.getInt(1)); }
        }
        return mesa;
    }

    public void atualizarEstado(int mesaId, EstadoMesa estado) throws SQLException {
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement("UPDATE mesas SET estado=? WHERE id=?")) {
            ps.setString(1, estado.name()); ps.setInt(2, mesaId); ps.executeUpdate();
        }
    }
}
