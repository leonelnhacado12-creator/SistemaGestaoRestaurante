package mz.uem.restaurante.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Configuração MySQL via variáveis de ambiente ou propriedades - nunca guardar passwords no Git. */
public final class Conexao {
    private Conexao() {}
    public static Connection obter() throws SQLException {
        String url = valor("RESTAURANTE_DB_URL", "restaurante.db.url", "jdbc:mysql://localhost:3306/gestao_restaurante?useSSL=false&serverTimezone=Africa/Maputo&allowPublicKeyRetrieval=true");
        String user = valor("RESTAURANTE_DB_USER", "restaurante.db.user", "root");
        String password = valor("RESTAURANTE_DB_PASSWORD", "restaurante.db.password", "");
        return DriverManager.getConnection(url, user, password);
    }
    /** Verifica a ligação sem deixar uma conexão aberta. */
    public static boolean testar() {
        try (Connection c = obter()) { return c.isValid(3); }
        catch (SQLException ex) { return false; }
    }
    private static String valor(String env, String prop, String padrao) {
        String v = System.getenv(env);
        if (v == null || v.isBlank()) v = System.getProperty(prop);
        return v == null || v.isBlank() ? padrao : v;
    }
}
