package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    private ItemPedido item(String sku, long preco, int qtd, int estoque, int peso, boolean fragil) {
        return new ItemPedido(sku, preco, qtd, estoque, peso, fragil);
    }

    @Test
    void deveLancarExcecaoParaListaNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(null, "PR", false, null));
    }

    @Test
    void deveLancarExcecaoParaListaComMaisDeCemItens() {
        List<ItemPedido> itens = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            itens.add(item("SKU" + i, 100, 1, 10, 100, false));
        }
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(itens, "PR", false, null));
    }

    @Test
    void deveAceitarListaComCemItens() {
        List<ItemPedido> itens = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            itens.add(item("SKU" + i, 100, 1, 10, 100, false));
        }
        Pedido pedido = new Pedido(itens, "PR", false, null);
        assertEquals(100, pedido.itens().size());
    }

    @Test
    void deveLancarExcecaoParaUfNula() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(List.of(), null, false, null));
    }

    @Test
    void deveLancarExcecaoParaUfComFormatoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(List.of(), "pr", false, null));
    }

    @Test
    void deveLancarExcecaoParaUfComTamanhoErrado() {
        assertThrows(IllegalArgumentException.class,
                () -> new Pedido(List.of(), "PRR", false, null));
    }

    @Test
    void naoDeveSerAfetadoPorMutacaoDaListaOriginal() {
        List<ItemPedido> itens = new ArrayList<>();
        itens.add(item("SKU1", 100, 1, 10, 100, false));
        Pedido pedido = new Pedido(itens, "PR", false, null);
        itens.add(item("SKU2", 100, 1, 10, 100, false));
        assertEquals(1, pedido.itens().size());
    }

    @Test
    void subtotalDeveIgnorarItensComQuantidadeZero() {
        Pedido pedido = new Pedido(List.of(
                item("SKU1", 1000, 0, 10, 100, false),
                item("SKU2", 500, 2, 10, 100, false)
        ), "PR", false, null);
        assertEquals(1000, pedido.subtotalCentavos());
    }

    @Test
    void pesoDeveSomarPesoDeTodosOsItens() {
        Pedido pedido = new Pedido(List.of(
                item("SKU1", 1000, 2, 10, 300, false),
                item("SKU2", 500, 1, 10, 100, false)
        ), "PR", false, null);
        assertEquals(700, pedido.pesoGramas());
    }

    @Test
    void temFragilDeveSerVerdadeiroQuandoItemFragilTemQuantidade() {
        Pedido pedido = new Pedido(List.of(
                item("SKU1", 1000, 1, 10, 100, true)
        ), "PR", false, null);
        assertTrue(pedido.temFragil());
    }

    @Test
    void temFragilDeveSerFalsoQuandoItemFragilTemQuantidadeZero() {
        Pedido pedido = new Pedido(List.of(
                item("SKU1", 1000, 0, 10, 100, true)
        ), "PR", false, null);
        assertFalse(pedido.temFragil());
    }

    @Test
    void temFragilDeveSerFalsoSemItensFrageis() {
        Pedido pedido = new Pedido(List.of(
                item("SKU1", 1000, 1, 10, 100, false)
        ), "PR", false, null);
        assertFalse(pedido.temFragil());
    }

    @Test
    void estoqueSuficienteDeveSerVerdadeiroQuandoTodosDisponiveis() {
        Pedido pedido = new Pedido(List.of(
                item("SKU1", 1000, 5, 5, 100, false)
        ), "PR", false, null);
        assertTrue(pedido.estoqueSuficiente());
    }

    @Test
    void estoqueSuficienteDeveSerFalsoQuandoAlgumItemFaltaEstoque() {
        Pedido pedido = new Pedido(List.of(
                item("SKU1", 1000, 5, 5, 100, false),
                item("SKU2", 1000, 6, 5, 100, false)
        ), "PR", false, null);
        assertFalse(pedido.estoqueSuficiente());
    }
}