package mz.uem.restaurante.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mz.uem.restaurante.model.ItemPedido;
import mz.uem.restaurante.model.Pedido;

/**
 * Gera um PDF de recibo autónomo com as fontes padrão do PDF. Assim, o projeto
 * mantém a exportação para PDF mesmo sem adicionar uma biblioteca externa.
 */
public final class ReciboPdfService {
    private static final DateTimeFormatter DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ReciboPdfService() { }

    public static Path exportar(Pedido pedido, Path destino) throws IOException {
        if (pedido == null) {
            throw new IllegalArgumentException("Seleccione um pedido para gerar o recibo.");
        }
        List<String> linhas = montarLinhas(pedido);
        byte[] conteudo = montarConteudo(linhas);
        Files.write(destino, montarPdf(conteudo));
        return destino;
    }

    public static String nomeFicheiro(Pedido pedido) {
        return "recibo_pedido_" + pedido.getId() + ".pdf";
    }

    private static List<String> montarLinhas(Pedido pedido) {
        List<String> linhas = new ArrayList<>();
        linhas.add("RESTAURANTE UEM");
        linhas.add("RECIBO DE PAGAMENTO");
        linhas.add("-----------------------------------------------");
        linhas.add("Pedido: #" + pedido.getId());
        linhas.add("Data: " + DATA.format(pedido.getDataHora()));
        linhas.add("Cliente: " + pedido.getCliente().getNome());
        linhas.add("Estado: " + pedido.getStatus());
        linhas.add("-----------------------------------------------");
        for (ItemPedido item : pedido.getItens()) {
            linhas.add(String.format(Locale.US, "%dx %s", item.getQuantidade(), item.getProduto().getNome()));
            linhas.add(String.format(Locale.US, "    %.2f MT", item.getSubtotal()));
        }
        linhas.add("-----------------------------------------------");
        linhas.add(String.format(Locale.US, "Subtotal: %.2f MT", pedido.getSubtotal()));
        linhas.add(String.format(Locale.US, "Desconto: %.2f MT", pedido.getDesconto()));
        linhas.add(String.format(Locale.US, "TOTAL: %.2f MT", pedido.getTotal()));
        linhas.add("Pagamento: " + pedido.getFormaPagamento());
        linhas.add("-----------------------------------------------");
        linhas.add("Obrigado pela preferencia.");
        linhas.add("Documento emitido pelo Sistema de Gestao de Restaurante.");
        return linhas;
    }

    private static byte[] montarConteudo(List<String> linhas) {
        StringBuilder conteudo = new StringBuilder("BT\n/F1 11 Tf\n50 790 Td\n15 TL\n");
        for (String linha : linhas) {
            conteudo.append('(').append(escapar(linha)).append(") Tj\nT*\n");
        }
        conteudo.append("ET\n");
        return conteudo.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    private static byte[] montarPdf(byte[] conteudo) throws IOException {
        ByteArrayOutputStream pdf = new ByteArrayOutputStream();
        List<Integer> offsets = new ArrayList<>();
        escrever(pdf, "%PDF-1.4\n%âãÏÓ\n");
        offsets.add(pdf.size());
        escrever(pdf, "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");
        offsets.add(pdf.size());
        escrever(pdf, "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n");
        offsets.add(pdf.size());
        escrever(pdf, "3 0 obj\n<< /Type /Page /Parent 2 0 R /MediaBox [0 0 595 842] /Resources << /Font << /F1 5 0 R >> >> /Contents 4 0 R >>\nendobj\n");
        offsets.add(pdf.size());
        escrever(pdf, "4 0 obj\n<< /Length " + conteudo.length + " >>\nstream\n");
        pdf.write(conteudo);
        escrever(pdf, "endstream\nendobj\n");
        offsets.add(pdf.size());
        escrever(pdf, "5 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\nendobj\n");
        int xref = pdf.size();
        escrever(pdf, "xref\n0 6\n0000000000 65535 f \n");
        for (int offset : offsets) {
            escrever(pdf, String.format(Locale.ROOT, "%010d 00000 n \n", offset));
        }
        escrever(pdf, "trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n" + xref + "\n%%EOF\n");
        return pdf.toByteArray();
    }

    private static String escapar(String texto) {
        String semAcentos = Normalizer.normalize(texto == null ? "" : texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\x20-\\x7E]", "?");
        return semAcentos.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)");
    }

    private static void escrever(ByteArrayOutputStream destino, String texto) throws IOException {
        destino.write(texto.getBytes(StandardCharsets.ISO_8859_1));
    }
}
