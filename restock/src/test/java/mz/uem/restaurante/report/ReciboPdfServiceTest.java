package mz.uem.restaurante.report;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import mz.uem.restaurante.model.Cliente;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.Produto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ReciboPdfServiceTest {
    @TempDir Path temporario;

    @Test
    void deveGerarPdfValidoParaUmPedido() throws Exception {
        Pedido pedido = new Pedido(25, new Cliente(1, "Ana", "ana", "", ""));
        pedido.adicionarItem(new Produto(10, "Sumo natural", "Bebida", 150, true), 2);
        pedido.definirResumoFinanceiro(300, 0, 300, "Dinheiro");
        Path pdf = ReciboPdfService.exportar(pedido, temporario.resolve("recibo.pdf"));
        assertTrue(Files.exists(pdf));
        byte[] bytes = Files.readAllBytes(pdf);
        assertArrayEquals("%PDF-1.4".getBytes(java.nio.charset.StandardCharsets.US_ASCII), java.util.Arrays.copyOf(bytes, 8));
    }
}
