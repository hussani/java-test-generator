# Java Test Generator
Gerador de testes automatizados utilizando programação por restrições.

Esta é a solução para um dos projetos da disciplina [SIN5022](https://uspdigital.usp.br/janus/componente/disciplinasOferecidasInicial.jsf?action=3&sgldis=SIN5022) discipline.

[Read in English here](./README.md)
## Introdução

Este projeto faz geração de testes automatizados utilizando de um código fonte Java. 

O código fonte é analisado, parseado e então o programa cria uma Gráfico de Fluxo de Controle (GFC) para cada método. Para cada GFC o gerador de caminhos lista todos os caminhos possíveis. Cara caminho possui um conjunto de expressões (ex. expressões aritiméticas ou lógicas).
Estas expressões são convertidas em restrições e resolvidas pelo programa. Após as restrições serem resolvidas os parâmetros de entrada e retornos de cada GFC são estimados e utilizados geração de testes.

## Como utilizar

### Requerimentos
- Java (JDK11+)
- Maven

### Instalação

Faça o download do projeto e execute os comandos abaixo.

```shell
mvn clean install
mvn package
```

Este processo irá gerar um arquivo `jar` com a aplicação.

### Executando

O comando a seguir cria um teste para o arquivo informado.
```shell
java -jar target/java-test-generator-0.1.0-SNAPSHOT.jar <caminho do arquivo java>
```
O nome do arquivo gerado por padrão é o nome da classe, acrescido do sufixo "Test.java".

Você pode alterar o nome do arquivo gerado informando um segundo argumento na execução,
como demonstrado no comando abaixo.
```shell
java -jar target/java-test-generator-0.1.0-SNAPSHOT.jar src/test/resources/If4Paths.java OutputFile.java
```
## Limitações
Este projeto não avançou muito na criação de GFC para todas as instruções Java válidas.
Atualmente a aplicação permite o uso de declarações `if` limitadas com:
- uma única expressão binária;
- sem block de `else`;
- com operadores inteiros.

O programa também permite o uso de declarações de retorno com literais inteiros ou expressões aritméticas.

## Melhorias futuras
- uso de expressões binárias com outras expressões binárias dentro
- uso de expressões else
- uso de laços (for, while)
- implementar exceções melhores
- melhorar o uso geral de estruturas de dados
- implementar testes unitários mais assertivos
- utilizar melhor o padrão visitor nos nós da árvore sintática abstrata
- uso de tipos genérios nos vértices e arestas da GFC

## Bibliotecas open source utilizadas
- [javaparser](https://github.com/javaparser/javaparser) - Utilizada para fazer parse da árvore síntática abstrata e para gera código de teste
- [choco-solver](https://github.com/chocoteam/choco-solver/) - Utilizada para resolver restrições
- [jgrapht](https://github.com/jgrapht/jgrapht) - Utilizada para gerar caminhos válidos para a GFC
- [JUnit 5](https://junit.org/junit5/) - Testes


