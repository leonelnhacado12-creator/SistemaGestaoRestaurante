package mz.uem.restaurante.report;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import mz.uem.restaurante.model.*;
import mz.uem.restaurante.service.DadosSistema;

/** Exporta listagens simples em CSV, compatíveis com Excel. */
public class RelatorioService {
    private RelatorioService() {}
    public static Path exportarProdutos(DadosSistema dados, Path destino) throws IOException {
        try(BufferedWriter w=Files.newBufferedWriter(destino,StandardCharsets.UTF_8)){
            w.write("ID;Nome;Categoria;Preço MT;Disponível\n");
            for(Produto p:dados.produtoService.listar())w.write(p.getId()+";\""+limpar(p.getNome())+"\";\""+limpar(p.getCategoria())+"\";"+p.getPreco()+";"+(p.isDisponivel()?"Sim":"Não")+"\n");
        }return destino;
    }
    public static Path exportarPromocoes(DadosSistema dados, Path destino) throws IOException {
        try(BufferedWriter w=Files.newBufferedWriter(destino,StandardCharsets.UTF_8)){
            w.write("ID;Nome;Desconto (%);Activa\n");
            for(Promocao p:dados.promocaoService.listar())w.write(p.getId()+";\""+limpar(p.getNome())+"\";"+p.getDescontoPercentual()+";"+(p.isAtiva()?"Sim":"Não")+"\n");
        }return destino;
    }
    public static Path exportarPedidos(DadosSistema dados, Path destino) throws IOException {
        try(BufferedWriter w=Files.newBufferedWriter(destino,StandardCharsets.UTF_8)){
            w.write("Pedido;Cliente;Data/Hora;Estado;Subtotal MT;Desconto MT;Total MT;Pagamento\n");
            for(Pedido p:dados.pedidoService.listar())w.write(p.getId()+";\""+limpar(p.getCliente().getNome())+"\";"+p.getDataHora()+";"+p.getStatus()+";"+p.getSubtotal()+";"+p.getDesconto()+";"+p.getTotal()+";\""+limpar(p.getFormaPagamento())+"\"\n");
        }return destino;
    }
    public static String nomeFicheiro(String prefixo){return prefixo+"_"+LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmm"))+".csv";}
    private static String limpar(String s){return s==null?"":s.replace("\"","\"\"").replace("\r"," ").replace("\n"," ");}
}
