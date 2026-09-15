package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AnaliseRiscoTest {

    private final AnaliseRisco risco = new AnaliseRisco();

    @Test
    void deveLancarExcecaoParaTotalNegativo() {
        Cliente cliente = new Cliente(false, false, 1);
        assertThrows(IllegalArgumentException.class,
                () -> risco.avaliar(cliente, -1, false));
    }

    @Test
    void clienteBloqueadoDeveSerRecusadoMesmoComTotalBaixo() {
        Cliente cliente = new Cliente(false, true, 5);
        assertEquals("RECUSADO", risco.avaliar(cliente, 100, false));
    }

    @Test
    void clienteNovoComTotalAcimaDoLimiteDeveIrParaRevisao() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("REVISAO", risco.avaliar(cliente, 100_001, false));
    }

    @Test
    void clienteNovoComTotalNoLimiteDeveSerAprovado() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("APROVADO", risco.avaliar(cliente, 100_000, false));
    }

    @Test
    void clienteNovoComPedidoExpressoDeveIrParaRevisaoMesmoComTotalBaixo() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("REVISAO", risco.avaliar(cliente, 100, true));
    }

    @Test
    void clienteNovoComTotalBaixoENaoExpressoDeveSerAprovado() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals("APROVADO", risco.avaliar(cliente, 100, false));
    }

    @Test
    void clienteComHistoricoETotalAcimaDoLimiteNaoVipDeveIrParaRevisao() {
        Cliente cliente = new Cliente(false, false, 3);
        assertEquals("REVISAO", risco.avaliar(cliente, 500_001, false));
    }

    @Test
    void clienteComHistoricoETotalNoLimiteDeveSerAprovado() {
        Cliente cliente = new Cliente(false, false, 3);
        assertEquals("APROVADO", risco.avaliar(cliente, 500_000, false));
    }

    @Test
    void clienteVipComHistoricoETotalAltoDeveSerAprovado() {
        Cliente cliente = new Cliente(true, false, 3);
        assertEquals("APROVADO", risco.avaliar(cliente, 900_000, false));
    }

    @Test
    void clienteComHistoricoETotalBaixoDeveSerAprovado() {
        Cliente cliente = new Cliente(false, false, 3);
        assertEquals("APROVADO", risco.avaliar(cliente, 1_000, false));
    }
}