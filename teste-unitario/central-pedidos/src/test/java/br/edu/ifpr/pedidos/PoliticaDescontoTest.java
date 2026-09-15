package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PoliticaDescontoTest {

    private final PoliticaDesconto politica = new PoliticaDesconto();

    private Cliente cliente(boolean vip, int comprasAnteriores) {
        return new Cliente(vip, false, comprasAnteriores);
    }

    @Test
    void deveLancarExcecaoParaSubtotalNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> politica.calcular(cliente(false, 1), -1, null));
    }

    @Test
    void clienteVipDeveReceberDezPorCento() {
        long desconto = politica.calcular(cliente(true, 1), 100_000, null);
        assertEquals(10_000, desconto);
    }

    @Test
    void clienteNaoVipComSubtotalNoLimiteDeveReceberCincoPorCento() {
        long desconto = politica.calcular(cliente(false, 1), 50_000, null);
        assertEquals(2_500, desconto);
    }

    @Test
    void clienteNaoVipComSubtotalAbaixoDoLimiteNaoDeveReceberDesconto() {
        long desconto = politica.calcular(cliente(false, 1), 49_999, null);
        assertEquals(0, desconto);
    }

    @Test
    void cupomNuloDeveManterDescontoBase() {
        long desconto = politica.calcular(cliente(false, 1), 50_000, null);
        assertEquals(2_500, desconto);
    }

    @Test
    void cupomEmBrancoDeveManterDescontoBase() {
        long desconto = politica.calcular(cliente(false, 1), 50_000, "   ");
        assertEquals(2_500, desconto);
    }

    @Test
    void cupomBemVindoDeveSerAplicadoParaClienteNovoComSubtotalSuficiente() {
        long desconto = politica.calcular(cliente(false, 0), 10_000, "BEMVINDO");
        assertEquals(2_000, desconto);
    }

    @Test
    void cupomBemVindoNaoDeveSerAplicadoParaClienteComComprasAnteriores() {
        long desconto = politica.calcular(cliente(false, 1), 10_000, "BEMVINDO");
        assertEquals(0, desconto);
    }

    @Test
    void cupomBemVindoNaoDeveSerAplicadoAbaixoDoSubtotalMinimo() {
        long desconto = politica.calcular(cliente(false, 0), 9_999, "BEMVINDO");
        assertEquals(0, desconto);
    }

    @Test
    void cupomDeveSerNormalizadoParaMaiusculasESemEspacos() {
        long desconto = politica.calcular(cliente(false, 0), 10_000, "  bemvindo  ");
        assertEquals(2_000, desconto);
    }

    @Test
    void cupomExtra10DeveSerAplicadoNoLimite() {
        long desconto = politica.calcular(cliente(false, 1), 20_000, "EXTRA10");
        assertEquals(2_000, desconto);
    }

    @Test
    void cupomExtra10NaoDeveSerAplicadoAbaixoDoLimite() {
        long desconto = politica.calcular(cliente(false, 1), 19_999, "EXTRA10");
        assertEquals(0, desconto);
    }

    @Test
    void cupomDesconhecidoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class,
                () -> politica.calcular(cliente(false, 1), 50_000, "INVALIDO"));
    }

    @Test
    void descontoNaoDevePassarDoTetoDeVintePorCento() {
        // vip (10%) + BEMVINDO (+2000) = 3000, mas o teto é 20% de 10_000 = 2000
        long desconto = politica.calcular(cliente(true, 0), 10_000, "BEMVINDO");
        assertEquals(2_000, desconto);
    }
}