package br.edu.ifpr.pedidos;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PedidoServiceTest {

    @Test
    void deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado() {
        // 1. Preparar: cliente comum, uma compra anterior e item disponível de R$ 100,00.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        // Simula o pagamento e registra as cobranças, sem banco ou serviço externo.
        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        // 2. Executar: percorrer um caminho completo do fechamento.
        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // 3. Verificar: sem desconto; frete de R$ 12,00; total de R$ 112,00.
        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(1_200L, resultado.freteCentavos()),
                () -> assertEquals(11_200L, resultado.totalCentavos()),
                // A lista comprova uma única cobrança, com o valor correto.
                () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void deveRecusarPedidoDeClienteBloqueadoSemGerarCobranca() {
        // Cliente bloqueado: o fechamento deve parar antes de calcular qualquer valor.
        Cliente cliente = new Cliente(false, true, 3);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("BLOQUEADO", resultado.status()),
                () -> assertEquals(0L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(0L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveLancarExcecaoQuandoPedidoNaoTemItensAtivosENaoDeveCobrar() {
        // Único item com quantidade zero: subtotal fica em zero, o que é tratado como erro.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 0, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        assertThrows(IllegalArgumentException.class, () -> service.fechar(pedido, cliente));
        assertTrue(cobrancas.isEmpty());
    }

    @Test
    void deveRetornarSemEstoqueQuandoQuantidadePedidaSuperaEstoqueENaoDeveCobrar() {
        // Estoque insuficiente: fechamento deve parar antes do cálculo de desconto/frete.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 5, 2, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("SEM_ESTOQUE", resultado.status()),
                () -> assertEquals(0L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(0L, resultado.freteCentavos()),
                () -> assertEquals(0L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveColocarPedidoExpressoDeClienteNovoEmRevisaoSemGerarCobranca() {
        // Cliente novo (0 compras) + pedido expresso: AnaliseRisco força REVISAO
        // mesmo com valor total baixo, e o pagamento não deve ser tentado.
        Cliente cliente = new Cliente(false, false, 0);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", true, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // Sem desconto; frete PR (1200) + adicional de expresso (1500) = 2700.
        assertAll(
                () -> assertEquals("REVISAO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(2_700L, resultado.freteCentavos()),
                () -> assertEquals(12_700L, resultado.totalCentavos()),
                () -> assertTrue(cobrancas.isEmpty())
        );
    }

    @Test
    void deveAplicarDescontoVipEFreteReduzidoComPagamentoAprovado() {
        // Cliente VIP: 10% de desconto e frete dividido pela metade.
        Cliente cliente = new Cliente(true, false, 5);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        // Desconto: 10% de 10_000 = 1_000 -> líquido 9_000.
        // Frete: base PR 1_200 / 2 (vip) = 600. Total: 9_000 + 600 = 9_600.
        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(1_000L, resultado.descontoCentavos()),
                () -> assertEquals(600L, resultado.freteCentavos()),
                () -> assertEquals(9_600L, resultado.totalCentavos()),
                () -> assertEquals(List.of(9_600L), cobrancas)
        );
    }

    @Test
    void devePagamentoRecusadoRegistrarUmaUnicaCobrancaSemNovaTentativa() {
        // O processador recusa definitivamente (retorna false): não há retry nesse caso,
        // apenas quando ocorre IllegalStateException (indisponibilidade temporária).
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            return false;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGAMENTO_RECUSADO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(1_200L, resultado.freteCentavos()),
                () -> assertEquals(11_200L, resultado.totalCentavos()),
                () -> assertEquals(List.of(11_200L), cobrancas)
        );
    }

    @Test
    void devePagarAposIndisponibilidadeTemporariaDoProcessadorRegistrandoDuasCobrancas() {
        // Primeira tentativa lança IllegalStateException (indisponibilidade temporária);
        // a segunda tentativa (dentro do limite de 3) aprova o pagamento.
        Cliente cliente = new Cliente(false, false, 1);
        ItemPedido item = new ItemPedido("LIVRO-JAVA", 10_000, 1, 5, 1_000, false);
        Pedido pedido = new Pedido(List.of(item), "PR", false, null);

        List<Long> cobrancas = new ArrayList<>();
        PedidoService service = new PedidoService(total -> {
            cobrancas.add(total);
            if (cobrancas.size() < 2) throw new IllegalStateException("Indisponível");
            return true;
        });

        ResultadoPedido resultado = service.fechar(pedido, cliente);

        assertAll(
                () -> assertEquals("PAGO", resultado.status()),
                () -> assertEquals(10_000L, resultado.subtotalCentavos()),
                () -> assertEquals(0L, resultado.descontoCentavos()),
                () -> assertEquals(1_200L, resultado.freteCentavos()),
                () -> assertEquals(11_200L, resultado.totalCentavos()),
                // Duas cobranças registradas, ambas com o mesmo valor total.
                () -> assertEquals(List.of(11_200L, 11_200L), cobrancas)
        );
    }
}