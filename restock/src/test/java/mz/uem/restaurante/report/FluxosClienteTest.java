package mz.uem.restaurante.report;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.time.LocalDateTime;
import mz.uem.restaurante.exception.ValidacaoException;
import mz.uem.restaurante.model.Mesa;
import mz.uem.restaurante.model.IngredienteReceita;
import mz.uem.restaurante.model.OperadoraCarteira;
import mz.uem.restaurante.model.Cliente;
import mz.uem.restaurante.model.EstadoPagamento;
import mz.uem.restaurante.model.Pedido;
import mz.uem.restaurante.model.Produto;
import mz.uem.restaurante.model.StatusPedido;
import mz.uem.restaurante.service.PedidoService;
import mz.uem.restaurante.service.ReservaService;
import mz.uem.restaurante.service.ReceitaService;
import mz.uem.restaurante.service.AuthService;
import mz.uem.restaurante.validation.Validador;
import org.junit.jupiter.api.Test;

class FluxosClienteTest {
    @Test
    void validaPrefixosDasCarteirasMoveis() {
        assertEquals("861234567", Validador.telefoneCarteira("+258 86 123 4567", OperadoraCarteira.EMOLA));
        assertEquals("851234567", Validador.telefoneCarteira("851234567", OperadoraCarteira.MPESA));
        assertEquals("821234567", Validador.telefoneCarteira("821234567", OperadoraCarteira.MKESH));
        assertThrows(ValidacaoException.class, () -> Validador.telefoneCarteira("861234567", OperadoraCarteira.MPESA));
    }

    @Test
    void naoPermiteReservaSobrepostaNemAcimaDaCapacidade() {
        ReservaService service = new ReservaService();
        Mesa mesa = service.guardarMesa(1, 4);
        LocalDateTime inicio = LocalDateTime.now().plusDays(1).withSecond(0).withNano(0);
        service.criarReserva("Ana", "841234567", mesa.getId(), inicio, 4);
        assertThrows(IllegalArgumentException.class, () -> service.criarReserva("Beto", "851234567", mesa.getId(), inicio.plusMinutes(30), 2));
        assertThrows(IllegalArgumentException.class, () -> service.criarReserva("Célia", "861234567", mesa.getId(), inicio.plusHours(3), 5));
        assertDoesNotThrow(() -> service.criarReserva("Dino", "871234567", mesa.getId(), inicio.plusHours(3), 2));
    }

    @Test
    void cozinhaSoIniciaDepoisDaConfirmacaoDoPagamento() throws Exception {
        PedidoService service = new PedidoService();
        Pedido pedido = service.criarPedido(new Cliente(1, "Ana", "ana", "", "841234567"));
        pedido.adicionarItem(new Produto(1, "Sumo", "Bebidas", 100, true), 1);
        service.confirmar(pedido, "M-Pesa (841234567)", 100, 0, 100, EstadoPagamento.EM_VALIDACAO);

        assertThrows(IllegalStateException.class, () -> service.atualizarStatus(pedido.getId(), StatusPedido.EM_PREPARACAO));
        service.atualizarEstadoPagamento(pedido.getId(), EstadoPagamento.CONFIRMADO);
        assertDoesNotThrow(() -> service.atualizarStatus(pedido.getId(), StatusPedido.EM_PREPARACAO));
    }

    @Test
    void fichaTecnicaNaoAceitaIngredientesRepetidos() {
        ReceitaService service = new ReceitaService();
        IngredienteReceita farinha = new IngredienteReceita(1, 4, "Farinha", "kg", 0.2);
        service.substituir(1, java.util.List.of(farinha));
        assertEquals(0.2, service.listar(1).get(0).getQuantidade());
        assertThrows(IllegalArgumentException.class, () -> service.substituir(1, java.util.List.of(farinha, farinha)));
    }

    @Test
    void naoPermiteMarcarFaltaAntesDoHorarioDaReserva() {
        ReservaService service = new ReservaService();
        Mesa mesa = service.guardarMesa(1, 2);
        var reserva = service.criarReserva("Ana", "841234567", mesa.getId(), LocalDateTime.now().plusDays(1), 2);
        assertThrows(IllegalStateException.class, () -> service.atualizarEstadoReserva(reserva.getId(), mz.uem.restaurante.model.EstadoReserva.NAO_COMPARECEU));
    }

    @Test
    void credenciaisPadraoFuncionamEMetodosLegadosNao() {
        AuthService service = new AuthService();
        assertDoesNotThrow(() -> { if (service.autenticar("admin", "admin123") == null) throw new AssertionError("admin123 deveria autenticar"); });
        assertEquals(null, service.autenticar("admin", "123"));
        assertEquals(null, service.autenticar("admin", "1234"));
        assertDoesNotThrow(() -> { if (service.autenticar("gerente", "gerente123") == null) throw new AssertionError("gerente123 deveria autenticar"); });
        assertDoesNotThrow(() -> { if (service.autenticar("atendente", "atendente123") == null) throw new AssertionError("atendente123 deveria autenticar"); });
    }
}
