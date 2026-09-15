package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class CalculadoraFreteTest {

    private final CalculadoraFrete calculadora = new CalculadoraFrete();
    private final Cliente naoVip = new Cliente(false, false, 1);
    private final Cliente vip = new Cliente(true, false, 1);

    private ItemPedido item(int peso, boolean fragil) {
        return new ItemPedido("SKU1", 1000, 1, 10, peso, fragil);
    }

    private Pedido pedido(String uf, boolean expresso, int peso, boolean fragil) {
        return new Pedido(List.of(item(peso, fragil)), uf, expresso, null);
    }

    @Test
    void deveLancarExcecaoParaLiquidoNegativo() {
        Pedido pedido = pedido("PR", false, 500, false);
        assertThrows(IllegalArgumentException.class,
                () -> calculadora.calcular(pedido, naoVip, -1));
    }

    @Test
    void deveCalcularFreteBaseParaPR() {
        assertEquals(1_200, calculadora.calcular(pedido("PR", false, 500, false), naoVip, 1_000));
    }

    @Test
    void deveCalcularFreteBaseParaSP() {
        assertEquals(2_000, calculadora.calcular(pedido("SP", false, 500, false), naoVip, 1_000));
    }

    @Test
    void deveCalcularFreteBaseParaRJ() {
        assertEquals(2_000, calculadora.calcular(pedido("RJ", false, 500, false), naoVip, 1_000));
    }

    @Test
    void deveCalcularFreteBaseParaOutrosEstados() {
        assertEquals(3_000, calculadora.calcular(pedido("MG", false, 500, false), naoVip, 1_000));
    }

    @Test
    void naoDeveAdicionarFreteExtraParaPesoAteDoisMilGramas() {
        assertEquals(1_200, calculadora.calcular(pedido("PR", false, 2_000, false), naoVip, 1_000));
    }

    @Test
    void deveAdicionarUmaFaixaDeFreteExtraLogoAcimaDoLimiteDePeso() {
        assertEquals(1_500, calculadora.calcular(pedido("PR", false, 2_001, false), naoVip, 1_000));
    }

    @Test
    void deveAdicionarDuasFaixasDeFreteExtraParaPesoBemAcimaDoLimite() {
        assertEquals(1_800, calculadora.calcular(pedido("PR", false, 3_001, false), naoVip, 1_000));
    }

    @Test
    void deveZerarFreteParaLiquidoAltoSemExpresso() {
        assertEquals(0, calculadora.calcular(pedido("PR", false, 500, false), naoVip, 30_000));
    }

    @Test
    void naoDeveZerarFreteParaLiquidoLogoAbaixoDoLimite() {
        assertEquals(1_200, calculadora.calcular(pedido("PR", false, 500, false), naoVip, 29_999));
    }

    @Test
    void naoDeveZerarFreteQuandoPedidoForExpressoMesmoComLiquidoAlto() {
        assertEquals(2_700, calculadora.calcular(pedido("PR", true, 500, false), naoVip, 30_000));
    }

    @Test
    void clienteVipDeveTerFreteDivididoPelaMetade() {
        assertEquals(600, calculadora.calcular(pedido("PR", false, 500, false), vip, 1_000));
    }

    @Test
    void pedidoExpressoDeveAdicionarValorExtra() {
        assertEquals(2_700, calculadora.calcular(pedido("PR", true, 500, false), naoVip, 1_000));
    }

    @Test
    void pedidoComItemFragilDeveAdicionarValorExtra() {
        assertEquals(1_700, calculadora.calcular(pedido("PR", false, 500, true), naoVip, 1_000));
    }

    @Test
    void deveCombinarTodosOsAjustesNaOrdemCorreta() {
        // base PR = 1200; vip divide por 2 = 600; expresso +1500 = 2100; fragil +500 = 2600
        assertEquals(2_600, calculadora.calcular(pedido("PR", true, 500, true), vip, 1_000));
    }
}