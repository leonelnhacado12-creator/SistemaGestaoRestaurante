package mz.uem.restaurante.report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import mz.uem.restaurante.database.Conexao;

/** Exporta um snapshot SQL de dados para restauração numa base criada pela aplicação. */
public final class BackupService {
    private static final List<String> TABELAS = List.of("clientes", "utilizadores", "produtos", "stock_itens", "fornecedores", "promocoes", "receitas_produto", "mesas", "reservas", "pedidos", "itens_pedido", "pagamentos", "notificacoes_cliente", "mensagens_atendimento", "movimentos_stock", "lotes_stock", "auditoria", "schema_migracoes");
    private BackupService() { }

    public static Path exportarSql(Path destino) throws SQLException, IOException {
        try (Connection c = Conexao.obter(); BufferedWriter escritor = Files.newBufferedWriter(destino, StandardCharsets.UTF_8)) {
            escritor.write("-- Backup Restaurante UEM gerado em " + LocalDateTime.now() + "\n");
            escritor.write("-- Execute após criar o esquema da aplicação.\nSET FOREIGN_KEY_CHECKS=0;\n\n");
            DatabaseMetaData meta = c.getMetaData();
            for (String tabela : TABELAS) {
                try (ResultSet existe = meta.getTables(c.getCatalog(), null, tabela, new String[]{"TABLE"})) {
                    if (!existe.next()) continue;
                }
                escreverTabela(c, tabela, escritor);
            }
            escritor.write("SET FOREIGN_KEY_CHECKS=1;\n");
        }
        return destino;
    }

    public static String nomeFicheiro() { return "backup_restaurante_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql"; }

    private static void escreverTabela(Connection c, String tabela, BufferedWriter escritor) throws SQLException, IOException {
        try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("SELECT * FROM " + tabela)) {
            ResultSetMetaData meta = rs.getMetaData(); int colunas = meta.getColumnCount(); boolean escreveuCabecalho = false;
            while (rs.next()) {
                if (!escreveuCabecalho) { escritor.write("-- " + tabela + "\n"); escreveuCabecalho = true; }
                StringBuilder sql = new StringBuilder("INSERT INTO `").append(tabela).append("` (");
                for (int i = 1; i <= colunas; i++) { if (i > 1) sql.append(','); sql.append('`').append(meta.getColumnLabel(i)).append('`'); }
                sql.append(") VALUES (");
                for (int i = 1; i <= colunas; i++) { if (i > 1) sql.append(','); sql.append(valor(rs.getObject(i))); }
                escritor.write(sql.append(");\n").toString());
            }
            if (escreveuCabecalho) escritor.write("\n");
        }
    }

    private static String valor(Object valor) {
        if (valor == null) return "NULL";
        if (valor instanceof Number || valor instanceof Boolean) return String.valueOf(valor);
        if (valor instanceof Timestamp timestamp) return "'" + timestamp.toString().replace("'", "''") + "'";
        return "'" + String.valueOf(valor).replace("\\", "\\\\").replace("'", "''") + "'";
    }
}
