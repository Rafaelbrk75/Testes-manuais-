# Resolução — Exercícios de Grafo de Fluxo de Controle (CFG)

---

## Exercício 1 — Classificação de pedido

```java
public String classificarPedido(
        double valor,
        boolean clienteVip,
        boolean pagamentoAprovado) {

    double desconto = 0;

    if (valor >= 500) {
        desconto = 10;
    }

    if (clienteVip) {
        desconto += 5;
    }

    if (!pagamentoAprovado) {
        return "PAGAMENTO RECUSADO";
    }

    double valorFinal = valor - (valor * desconto / 100);
    return "PEDIDO APROVADO: " + valorFinal;
}
```

### 1. Blocos básicos

| Bloco | Conteúdo |
|-------|----------|
| B1 | `desconto = 0;` seguido da decisão `if (valor >= 500)` |
| B2 | `desconto = 10;` (ramo verdadeiro de B1) |
| B3 | decisão `if (clienteVip)` (ponto de junção após B1/B2) |
| B4 | `desconto += 5;` (ramo verdadeiro de B3) |
| B5 | decisão `if (!pagamentoAprovado)` (ponto de junção após B3/B4) |
| B6 | `return "PAGAMENTO RECUSADO";` (ramo verdadeiro de B5 — saída antecipada) |
| B7 | `valorFinal = ...; return "PEDIDO APROVADO: " + valorFinal;` (ramo falso de B5) |

### 2. Decisões

- **D1** — `valor >= 500`
- **D2** — `clienteVip`
- **D3** — `!pagamentoAprovado`

### 3–4. Grafo de Fluxo de Controle

Um nó adicional **"fim"** (B8) unifica os dois pontos de `return` (o de B6 e o de B7), representando o encerramento único do método.

```
B1 --F--> B3
B1 --V--> B2 --> B3
B3 --F--> B5
B3 --V--> B4 --> B5
B5 --F--> B7 --> fim
B5 --V--> B6 --> fim
```

O `return` antecipado dentro de D3 aparece como uma aresta direta de B5 para o nó "fim", sem passar por B7.

### 5. Nós e arestas

- **N = 8** (B1 a B7 + fim)
- **E = 10**

Lista de arestas: B1→B2, B1→B3, B2→B3, B3→B4, B3→B5, B4→B5, B5→B6, B5→B7, B6→fim, B7→fim.

### 6–7. Complexidade ciclomática

```
V(G) = E - N + 2 = 10 - 8 + 2 = 4
V(G) = número de decisões + 1 = 3 + 1 = 4
```

Os dois cálculos coincidem ✔

### 8–10. Base de caminhos independentes

| # | Caminho | valor | clienteVip | pagamentoAprovado | Resultado esperado |
|---|---------|-------|------------|--------------------|----------------------|
| 1 | B1(F)→B3(F)→B5(F)→B7 | 100 | false | true | `PEDIDO APROVADO: 100.0` |
| 2 | B1(V)→B2→B3(F)→B5(F)→B7 | 600 | false | true | `PEDIDO APROVADO: 540.0` |
| 3 | B1(F)→B3(V)→B4→B5(F)→B7 | 100 | true | true | `PEDIDO APROVADO: 95.0` |
| 4 | B1(F)→B3(F)→B5(V)→B6 | 100 | false | false | `PAGAMENTO RECUSADO` |

### Questões para discussão

**Quantas combinações entre as três condições são possíveis?**
2³ = **8 combinações** (verdadeiro/falso para cada uma das três condições).

**O número de combinações possíveis é igual à complexidade ciclomática? Explique.**
Não. V(G) = 4, mas há 8 combinações booleanas possíveis. A complexidade ciclomática mede o número mínimo de caminhos *linearmente independentes* — uma base a partir da qual todos os demais caminhos podem ser obtidos por combinação de arestas —, não o total de combinações de entrada. Como as três decisões aqui são sequenciais (não aninhadas umas dentro das outras), cada uma contribui apenas um grau de liberdade extra ao grafo, resultando em `3 decisões + 1 = 4` caminhos-base, mesmo havendo 8 combinações possíveis de valores de entrada.

**Como o `return` dentro da terceira condição altera o grafo?**
Ele cria uma aresta de saída antecipada: o ramo verdadeiro de B5 vai direto para o nó "fim", sem passar pelo bloco de cálculo de `valorFinal`. Isso "fecha" esse ramo do grafo mais cedo, mas não altera a contagem de decisões nem a fórmula da complexidade ciclomática.

**É possível executar o cálculo de `valorFinal` quando o pagamento não foi aprovado?**
Não. O ramo verdadeiro de B5 (`!pagamentoAprovado` verdadeiro) vai direto para B6 (`return recusado`) e nunca alcança B7, onde `valorFinal` é calculado.

---

## Exercício 2 — Análise de leituras de temperatura

```java
public int contarAlertas(double[] temperaturas) {
    int alertas = 0;
    int i = 0;

    while (i < temperaturas.length) {
        if (temperaturas[i] < 0) {
            alertas += 2;
        } else if (temperaturas[i] > 35) {
            alertas++;
        }

        i++;
    }

    return alertas;
}
```

### 1. Blocos básicos

| Bloco | Conteúdo |
|-------|----------|
| B1 | `alertas = 0; i = 0;` (inicialização) |
| B2 | decisão `while (i < temperaturas.length)` |
| B3 | decisão `if (temperaturas[i] < 0)` |
| B4 | `alertas += 2;` (ramo verdadeiro de B3) |
| B5 | decisão `else if (temperaturas[i] > 35)` (ramo falso de B3) |
| B6 | `alertas++;` (ramo verdadeiro de B5) |
| B7 | `i++;` (ponto de junção após B3/B4/B5/B6, antes de voltar ao laço) |
| B8 | `return alertas;` (saída do laço) |

### 2. Decisões associadas

- **D1** — condição do `while` (`i < temperaturas.length`)
- **D2** — primeiro `if` (`temperaturas[i] < 0`)
- **D3** — `else if` (`temperaturas[i] > 35`)

### 3–4. Grafo de Fluxo de Controle

```
B1 --> B2
B2 --V(entra no laço)--> B3
B2 --F(sai do laço)-----> B8
B3 --F--> B5
B3 --V--> B4 --> B7
B5 --F--> B7
B5 --V--> B6 --> B7
B7 --(retorno do laço)--> B2
```

A aresta de retorno é **B7 → B2**: depois de incrementar `i`, o fluxo volta a avaliar a condição do `while`.

### 5. Nós e arestas

- **N = 8** (B1 a B8)
- **E = 10**

Lista de arestas: B1→B2, B2→B3, B2→B8, B3→B4, B3→B5, B4→B7, B5→B6, B5→B7, B6→B7, B7→B2.

### 6. Complexidade ciclomática

```
V(G) = E - N + 2 = 10 - 8 + 2 = 4
V(G) = número de decisões + 1 = 3 + 1 = 4
```

Os dois cálculos coincidem ✔

### 7–9. Base de caminhos independentes

| # | Caminho | Vetor de entrada | Resultado esperado |
|---|---------|-------------------|----------------------|
| 1 | B1→B2(F)→B8 | `{}` (vazio) | `0` |
| 2 | B1→B2(V)→B3(V)→B4→B7→B2(F)→B8 | `{-5}` | `2` |
| 3 | B1→B2(V)→B3(F)→B5(V)→B6→B7→B2(F)→B8 | `{40}` | `1` |
| 4 | B1→B2(V)→B3(F)→B5(F)→B7→B2(F)→B8 | `{20}` | `0` |

### 10. Por que o retorno do laço precisa aparecer no CFG?

Porque é ele que representa a repetição: sem a aresta B7→B2, o grafo descreveria apenas uma única passagem pelo corpo do laço, e não captaria o comportamento real do `while` (que pode executar zero, uma ou várias vezes). É essa aresta de retorno que introduz o "grau de liberdade" extra contado na complexidade ciclomática associada à decisão D1.

### Questões para discussão

**Um vetor com várias temperaturas percorre um único caminho ou pode repetir partes do grafo?**
Repete partes do grafo. A cada iteração do `while`, o fluxo passa novamente por B2→B3→(B4 ou B5→B6 ou nenhum)→B7. Um único vetor de teste, com várias posições, pode exercitar vários caminhos-base diferentes ao longo de suas iterações — não fica restrito a um único caminho estático.

**Qual entrada permite sair do método sem acessar uma posição do vetor?**
Um vetor vazio (`temperaturas.length == 0`), pois a condição do `while` já é falsa na primeira avaliação, e o corpo do laço nunca é executado.

**Os testes dos valores `0` e `35` ajudam a avaliar quais fronteiras?**
São testes de valor-limite (boundary testing) das duas condições: `0` testa a fronteira de `< 0` (não deve gerar alerta, pois `0` não é menor que `0`), e `35` testa a fronteira de `> 35` (também não deve gerar alerta, pois `35` não é maior que `35`).

**Por que o `else if` deve ser representado como uma nova decisão?**
Porque é uma condição distinta, avaliada apenas quando a condição do `if` anterior é falsa. Ela introduz um novo nó com duas saídas (verdadeiro/falso) no grafo e, portanto, conta como uma decisão separada — tanto na contagem de arestas quanto na fórmula `V(G) = decisões + 1`.
