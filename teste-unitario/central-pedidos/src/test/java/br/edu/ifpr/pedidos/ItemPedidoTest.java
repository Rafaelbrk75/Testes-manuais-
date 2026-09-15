package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ItemPedidoTest {

    @Test
    void deveCriarItemValido() {
        ItemPedido item = new ItemPedido("SKU1", 1000, 2, 10, 500, false);
        assertEquals(2000, item.totalCentavos());
    }

    @Test
    void deveLancarExcecaoParaSkuNulo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido(null, 1000, 1, 10, 500, false));
    }

    @Test
    void deveLancarExcecaoParaSkuEmBranco() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("   ", 1000, 1, 10, 500, false));
    }

    @Test
    void deveLancarExcecaoParaPrecoZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU1", 0, 1, 10, 500, false));
    }

    @Test
    void deveAceitarPrecoNoLimiteSuperior() {
        ItemPedido item = new ItemPedido("SKU1", 1_000_000, 1, 10, 500, false);
        assertEquals(1_000_000, item.totalCentavos());
    }

    @Test
    void deveLancarExcecaoParaPrecoAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU1", 1_000_001, 1, 10, 500, false));
    }

    @Test
    void deveLancarExcecaoParaQuantidadeNegativa() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU1", 1000, -1, 10, 500, false));
    }

    @Test
    void deveAceitarQuantidadeZero() {
        ItemPedido item = new ItemPedido("SKU1", 1000, 0, 10, 500, false);
        assertEquals(0, item.totalCentavos());
    }

    @Test
    void deveAceitarQuantidadeNoLimiteSuperior() {
        ItemPedido item = new ItemPedido("SKU1", 1000, 100, 200, 500, false);
        assertEquals(100_000, item.totalCentavos());
    }

    @Test
    void deveLancarExcecaoParaQuantidadeAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU1", 1000, 101, 200, 500, false));
    }

    @Test
    void deveLancarExcecaoParaEstoqueNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU1", 1000, 1, -1, 500, false));
    }

    @Test
    void deveLancarExcecaoParaPesoZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU1", 1000, 1, 10, 0, false));
    }

    @Test
    void deveLancarExcecaoParaPesoAcimaDoLimite() {
        assertThrows(IllegalArgumentException.class,
                () -> new ItemPedido("SKU1", 1000, 1, 10, 100_001, false));
    }

    @Test
    void deveEstarDisponivelQuandoQuantidadeMenorOuIgualAoEstoque() {
        ItemPedido item = new ItemPedido("SKU1", 1000, 5, 5, 500, false);
        assertTrue(item.disponivel());
    }

    @Test
    void naoDeveEstarDisponivelQuandoQuantidadeMaiorQueEstoque() {
        ItemPedido item = new ItemPedido("SKU1", 1000, 6, 5, 500, false);
        assertFalse(item.disponivel());
    }
}