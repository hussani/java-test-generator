# Java Test Generator
Automatic test generator using constraint programming.

This is a solution for one of the projects of [SIN5022](https://uspdigital.usp.br/janus/componente/disciplinasOferecidasInicial.jsf?action=3&sgldis=SIN5022) discipline.

[Leia em português clicando aqui](./README-pt_BR.md)
## What

This project generates automated tests from Java source code.

The source code is analyzed, parsed then the program generates a control flow graph (CFG) for each method. Given a CFG path generator lists all possible paths. Each path has a set of expressions (e.g., arithmetic or logic).
These expressions are converted into constraints and solved by the program. After the constraint solving, the input parameters and returns of the CFG are evaluated to test solution generation.

## Usage

### Requirements
- Java (JDK11+)
- Maven

### Install

Download the project and run the following commands

```shell
mvn clean install
mvn package
```

This process will generate a `jar` file with the application.

### Run

The following command generates a test to the given file.
```shell
java -jar target/java-test-generator-0.1.0-SNAPSHOT.jar <java file path>
```
The default test file name is the java class name with suffix "Test.java"

You can change the output file with a second argument, like the command below.
```shell
java -jar target/java-test-generator-0.1.0-SNAPSHOT.jar src/test/resources/If4Paths.java OutputFile.java
```
## Limitations
This project doesn't advance on generates CFG of all Java instructions. Currently, the program supports limited `if` blocks with:
- a single binary expression;
- without else statements;
- with integer operators.

The program also supports return statements with integer literals and arithmetic expressions.

## Further improvements
- handle binary expressions with binary expressions within
- handle else statements
- handle loops
- implement better exceptions
- improve general data structures
- implement assertive tests
- better usage of visitor pattern on AST nodes
- usage of generic types on edges and vertex of CFG

## Used Open Source Libraries
- [javaparser](https://github.com/javaparser/javaparser) - Used to parse source code into AST and code generation
- [choco-solver](https://github.com/chocoteam/choco-solver/) - Used to solve constraints and general constraint programming
- [jgrapht](https://github.com/jgrapht/jgrapht) - Used to generate valid CFG paths
- [JUnit 5](https://junit.org/junit5/) - Tests


