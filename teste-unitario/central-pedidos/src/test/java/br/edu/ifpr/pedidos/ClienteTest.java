package br.edu.ifpr.pedidos;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void deveCriarClienteComHistoricoValido() {
        Cliente cliente = new Cliente(false, false, 0);
        assertEquals(0, cliente.comprasAnteriores());
    }

    @Test
    void deveLancarExcecaoParaHistoricoNegativo() {
        assertThrows(IllegalArgumentException.class,
                () -> new Cliente(false, false, -1));
    }
}