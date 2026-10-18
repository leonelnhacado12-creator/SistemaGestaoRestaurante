package mz.uem.restaurante.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class InicializadorBD {
    private InicializadorBD() {}
    public static void criarTabelas() throws SQLException {
        try (Connection c = Conexao.obter(); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS produtos (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, categoria VARCHAR(80) NOT NULL, preco DECIMAL(12,2) NOT NULL, disponivel BOOLEAN NOT NULL DEFAULT TRUE, stock INT NOT NULL DEFAULT 100, criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS clientes (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, telefone VARCHAR(30), username VARCHAR(80) UNIQUE, criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS promocoes (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, desconto_percentual DECIMAL(5,2) NOT NULL, ativa BOOLEAN NOT NULL DEFAULT TRUE, data_inicio DATE NULL, data_fim DATE NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS pedidos (id BIGINT AUTO_INCREMENT PRIMARY KEY, cliente_id INT NULL, nome_cliente VARCHAR(120) NOT NULL, data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, estado VARCHAR(30) NOT NULL, subtotal DECIMAL(12,2) NOT NULL DEFAULT 0, desconto DECIMAL(12,2) NOT NULL DEFAULT 0, total DECIMAL(12,2) NOT NULL DEFAULT 0, forma_pagamento VARCHAR(30), FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE SET NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS itens_pedido (id BIGINT AUTO_INCREMENT PRIMARY KEY, pedido_id BIGINT NOT NULL, produto_id INT NULL, nome_produto VARCHAR(120) NOT NULL, quantidade INT NOT NULL, preco_unitario DECIMAL(12,2) NOT NULL, subtotal DECIMAL(12,2) NOT NULL, FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE, FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE SET NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS pagamentos (id BIGINT AUTO_INCREMENT PRIMARY KEY, pedido_id BIGINT NOT NULL, forma VARCHAR(30) NOT NULL, valor DECIMAL(12,2) NOT NULL, data_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, estado VARCHAR(30) NOT NULL, FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS stock_itens (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, categoria VARCHAR(80) NOT NULL, unidade VARCHAR(20) NOT NULL, quantidade DECIMAL(14,3) NOT NULL DEFAULT 0, stock_minimo DECIMAL(14,3) NOT NULL DEFAULT 0, criado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP, actualizado_em TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS movimentos_stock (id BIGINT AUTO_INCREMENT PRIMARY KEY, item_id INT NOT NULL, tipo VARCHAR(20) NOT NULL, quantidade DECIMAL(14,3) NOT NULL, saldo_depois DECIMAL(14,3) NOT NULL, motivo VARCHAR(255), utilizador VARCHAR(80) NOT NULL DEFAULT 'gerente', data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP, FOREIGN KEY (item_id) REFERENCES stock_itens(id) ON DELETE RESTRICT)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS utilizadores (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, username VARCHAR(80) NOT NULL UNIQUE, senha_hash VARCHAR(255) NOT NULL, perfil VARCHAR(30) NOT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS mesas (id INT AUTO_INCREMENT PRIMARY KEY, numero INT NOT NULL UNIQUE, lugares INT NOT NULL, estado VARCHAR(20) NOT NULL DEFAULT 'LIVRE')");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS reservas (id INT AUTO_INCREMENT PRIMARY KEY, mesa_id INT NOT NULL, cliente VARCHAR(120) NOT NULL, telefone VARCHAR(30), data_hora DATETIME NOT NULL, pessoas INT NOT NULL, estado VARCHAR(20) NOT NULL DEFAULT 'PENDENTE', FOREIGN KEY (mesa_id) REFERENCES mesas(id) ON DELETE RESTRICT)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS auditoria (id BIGINT AUTO_INCREMENT PRIMARY KEY, utilizador VARCHAR(80) NOT NULL, operacao VARCHAR(40) NOT NULL, entidade VARCHAR(60) NOT NULL, detalhe VARCHAR(255), data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS fornecedores (id INT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(120) NOT NULL, telefone VARCHAR(30), email VARCHAR(120), activo BOOLEAN NOT NULL DEFAULT TRUE)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS lotes_stock (id INT AUTO_INCREMENT PRIMARY KEY, item_id INT NOT NULL, fornecedor_id INT NULL, lote VARCHAR(60), quantidade DECIMAL(14,3) NOT NULL, validade DATE NULL, FOREIGN KEY (item_id) REFERENCES stock_itens(id) ON DELETE CASCADE, FOREIGN KEY (fornecedor_id) REFERENCES fornecedores(id) ON DELETE SET NULL)");
        }
    }
}
