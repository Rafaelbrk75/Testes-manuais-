package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceTest {

    @Test
    void deveLancarExcecaoParaTotalZeroOuNegativo() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(0, 1));
    }

    @Test
    void deveLancarExcecaoParaMaxTentativasMenorQueUm() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(1000, 0));
    }

    @Test
    void deveLancarExcecaoParaMaxTentativasMaiorQueTres() {
        PagamentoService service = new PagamentoService(total -> true);
        assertThrows(IllegalArgumentException.class, () -> service.pagar(1000, 4));
    }

    @Test
    void deveAprovarNaPrimeiraTentativa() {
        int[] chamadas = {0};
        PagamentoService service = new PagamentoService(total -> {
            chamadas[0]++;
            return true;
        });
        assertTrue(service.pagar(1000, 3));
        assertEquals(1, chamadas[0]);
    }

    @Test
    void deveRecusarDefinitivamenteSemTentarNovamente() {
        int[] chamadas = {0};
        PagamentoService service = new PagamentoService(total -> {
            chamadas[0]++;
            return false;
        });
        assertFalse(service.pagar(1000, 3));
        assertEquals(1, chamadas[0]);
    }

    @Test
    void deveTentarNovamenteAposIndisponibilidadeTemporariaEDepoisAprovar() {
        int[] chamadas = {0};
        PagamentoService service = new PagamentoService(total -> {
            chamadas[0]++;
            if (chamadas[0] < 2) throw new IllegalStateException("Indisponível");
            return true;
        });
        assertTrue(service.pagar(1000, 3));
        assertEquals(2, chamadas[0]);
    }

    @Test
    void deveRetornarFalsoAposEsgotarTentativasComIndisponibilidadeConstante() {
        int[] chamadas = {0};
        PagamentoService service = new PagamentoService(total -> {
            chamadas[0]++;
            throw new IllegalStateException("Indisponível");
        });
        assertFalse(service.pagar(1000, 3));
        assertEquals(3, chamadas[0]);
    }
}