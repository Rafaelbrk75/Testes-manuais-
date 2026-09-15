package br.edu.ifpr.boletim;

import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BoletimTest {

    @Test
    void deveAprovarAlunoComMediaOito() {
        // Preparar: criar o objeto que será testado.
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(8);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("APROVADO", resultado);
    }

    @Test
    void deveRecuperarNotaAlunoComMediaQuatro() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(4);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("RECUPERACAO", resultado);
    }

    @Test
    void deveReprovarAlunoComMediaDois() {
        Boletim boletim = new Boletim();

        // Executar: chamar um único método com uma entrada conhecida.
        String resultado = boletim.verificarSituacao(2);

        // Verificar: comparar o resultado esperado com o resultado obtido.
        assertEquals("REPROVADO", resultado);
    }

    @Test
    void deveCalcularMediaIgualCinco() {
        Boletim boletim = new Boletim();

        double resultado = boletim.calcularMedia(5,5);

        assertEquals(5,resultado);

    }

    @Test
    void deveRetornarZeroParaArrayVazio() {
        Boletim boletim = new Boletim();
        double[] medias = {};
        int resultado = boletim.contarAprovados(medias);
        assertEquals(0, resultado);
    }


    @Test
    void deveContarApenasOsAlunosAprovados() {
        Boletim boletim = new Boletim();
        double[] medias = {7, 5, 8, 3};
        int resultado = boletim.contarAprovados(medias);
        assertEquals(2, resultado);
    }

    @Test
    void deveRetornarZeroQuandoNaoHouverAprovados() {
        Boletim boletim = new Boletim();
        double[] medias = {5, 6, 4, 3};
        int resultado = boletim.contarAprovados(medias);
        assertEquals(0, resultado);
    }
    // TODO: escrever os próximos testes durante a aula.
}