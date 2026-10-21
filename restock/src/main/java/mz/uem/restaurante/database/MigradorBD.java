package mz.uem.restaurante.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import mz.uem.restaurante.security.PasswordHasher;

/** Mantém um histórico de versões do esquema, permitindo actualizar instalações existentes sem apagar dados. */
public final class MigradorBD {
    private MigradorBD() { }

    public static void aplicar(Connection conexao) throws SQLException {
        try (Statement s = conexao.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS schema_migracoes (versao INT PRIMARY KEY, aplicada_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP, descricao VARCHAR(255) NOT NULL)");
        }
        aplicarSeNecessario(conexao, 1, "Versionamento inicial do esquema");
        if (!estaAplicada(conexao, 2)) {
            try (Statement s = conexao.createStatement()) {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS receitas_produto (produto_id INT NOT NULL, item_stock_id INT NOT NULL, quantidade DECIMAL(14,3) NOT NULL, PRIMARY KEY(produto_id,item_stock_id), FOREIGN KEY(produto_id) REFERENCES produtos(id) ON DELETE CASCADE, FOREIGN KEY(item_stock_id) REFERENCES stock_itens(id) ON DELETE RESTRICT)");
            }
            registar(conexao, 2, "Fichas técnicas e baixa de ingredientes");
        }
        if (!estaAplicada(conexao, 3)) {
            adicionarColunaLembreteSeNecessario(conexao);
            registar(conexao, 3, "Lembretes de reserva sem duplicação");
        }
        if (!estaAplicada(conexao, 4)) {
            actualizarCredenciaisLegadas(conexao);
            registar(conexao, 4, "Remoção de credenciais legadas de acesso");
        }
        if (!estaAplicada(conexao, 5)) {
            garantirContasDemonstracao(conexao);
            registar(conexao, 5, "Normalização das contas demonstrativas");
        }
    }

    private static void aplicarSeNecessario(Connection c, int versao, String descricao) throws SQLException {
        if (estaAplicada(c, versao)) return;
        registar(c, versao, descricao);
    }

    private static boolean estaAplicada(Connection c, int versao) throws SQLException {
        try (PreparedStatement consulta = c.prepareStatement("SELECT 1 FROM schema_migracoes WHERE versao=?")) {
            consulta.setInt(1, versao);
            try (ResultSet rs = consulta.executeQuery()) { return rs.next(); }
        }
    }
    private static void registar(Connection c, int versao, String descricao) throws SQLException {
        try (PreparedStatement inserir = c.prepareStatement("INSERT INTO schema_migracoes(versao,descricao) VALUES(?,?)")) {
            inserir.setInt(1, versao); inserir.setString(2, descricao); inserir.executeUpdate();
        }
    }
    private static void adicionarColunaLembreteSeNecessario(Connection c) throws SQLException {
        try (ResultSet colunas = c.getMetaData().getColumns(c.getCatalog(), null, "reservas", "lembrete_enviado")) {
            if (colunas.next()) return;
        }
        try (Statement s = c.createStatement()) { s.executeUpdate("ALTER TABLE reservas ADD COLUMN lembrete_enviado BOOLEAN NOT NULL DEFAULT FALSE"); }
    }

    /** Converte apenas palavras-passe demonstrativas antigas; contas personalizadas nunca são alteradas. */
    private static void actualizarCredenciaisLegadas(Connection c) throws SQLException {
        actualizarSeLegada(c, "admin", "admin123", "123", "1234");
        actualizarSeLegada(c, "gerente", "gerente123");
        actualizarSeLegada(c, "atendente", "atendente123");
    }

    private static void actualizarSeLegada(Connection c, String username, String senhaActual, String... legadas) throws SQLException {
        StringBuilder sql = new StringBuilder("UPDATE utilizadores SET senha_hash=? WHERE LOWER(username)=? AND senha_hash IN (?, ?");
        for (int i = 0; i < legadas.length; i++) sql.append(", ?, ?");
        sql.append(")");
        try (PreparedStatement actualizar = c.prepareStatement(sql.toString())) {
            actualizar.setString(1, PasswordHasher.hash(senhaActual));
            actualizar.setString(2, username.toLowerCase());
            actualizar.setString(3, senhaActual);
            actualizar.setString(4, PasswordHasher.hash(senhaActual));
            int indice = 5;
            for (String legada : legadas) {
                actualizar.setString(indice++, legada);
                actualizar.setString(indice++, PasswordHasher.hash(legada));
            }
            actualizar.executeUpdate();
        }
    }

    /** Garante uma única vez as contas iniciais, inclusive em bases criadas por versões antigas. */
    private static void garantirContasDemonstracao(Connection c) throws SQLException {
        garantirConta(c, "Administrador do Sistema", "admin", "admin123", "ADMINISTRADOR");
        garantirConta(c, "Gerente do Restaurante", "gerente", "gerente123", "GERENTE");
        garantirConta(c, "Atendente", "atendente", "atendente123", "ATENDENTE");
    }

    private static void garantirConta(Connection c, String nome, String username, String senha, String perfil) throws SQLException {
        try (PreparedStatement actualizar = c.prepareStatement("UPDATE utilizadores SET nome=?,senha_hash=?,perfil=? WHERE LOWER(username)=?")) {
            actualizar.setString(1, nome); actualizar.setString(2, PasswordHasher.hash(senha)); actualizar.setString(3, perfil); actualizar.setString(4, username);
            if (actualizar.executeUpdate() > 0) return;
        }
        try (PreparedStatement inserir = c.prepareStatement("INSERT INTO utilizadores(nome,username,senha_hash,perfil) VALUES(?,?,?,?)")) {
            inserir.setString(1, nome); inserir.setString(2, username); inserir.setString(3, PasswordHasher.hash(senha)); inserir.setString(4, perfil); inserir.executeUpdate();
        }
    }
}
