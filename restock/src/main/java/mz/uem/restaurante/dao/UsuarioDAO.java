package mz.uem.restaurante.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mz.uem.restaurante.database.Conexao;
import mz.uem.restaurante.security.PerfilAcesso;
import mz.uem.restaurante.security.UsuarioSistema;

/** Acesso JDBC isolado para contas internas e respetivos perfis. */
public class UsuarioDAO {
    public List<UsuarioSistema> listar() throws SQLException {
        List<UsuarioSistema> resultado = new ArrayList<>();
        String sql = "SELECT id,nome,username,senha_hash,perfil FROM utilizadores ORDER BY nome";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                resultado.add(mapear(rs));
            }
        }
        return resultado;
    }

    public UsuarioSistema guardar(UsuarioSistema usuario) throws SQLException {
        boolean novo = usuario.getId() <= 0;
        String sql = novo
                ? "INSERT INTO utilizadores(nome,username,senha_hash,perfil) VALUES(?,?,?,?)"
                : "UPDATE utilizadores SET nome=?,username=?,senha_hash=?,perfil=? WHERE id=?";
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getNome());
            ps.setString(2, usuario.getUsername());
            ps.setString(3, usuario.getSenhaHash());
            ps.setString(4, usuario.getPerfil().name());
            if (!novo) {
                ps.setInt(5, usuario.getId());
            }
            ps.executeUpdate();
            if (novo) {
                try (ResultSet chaves = ps.getGeneratedKeys()) {
                    if (chaves.next()) {
                        usuario.setId(chaves.getInt(1));
                    }
                }
            }
        }
        return usuario;
    }

    public void remover(int id) throws SQLException {
        try (Connection c = Conexao.obter(); PreparedStatement ps = c.prepareStatement("DELETE FROM utilizadores WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private UsuarioSistema mapear(ResultSet rs) throws SQLException {
        return new UsuarioSistema(rs.getInt("id"), rs.getString("nome"), rs.getString("username"),
                rs.getString("senha_hash"), PerfilAcesso.valueOf(rs.getString("perfil")));
    }
}
