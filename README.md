# Java Test Generator
Automatic test generator using symbolic execution.

This is a solution for one of the projects of [SIN5022](https://uspdigital.usp.br/janus/componente/disciplinasOferecidasInicial.jsf?action=3&sgldis=SIN5022) discipline.

## What

This project generates automated tests from Java source code.

The source code is analyzed, parsed then the program generates a control flow graph (CFG) for each method. Given a CFG path generator lists all possible paths. Each path has a set of expressions (e.g., arithmetic or logic).
These expressions are converted into constraints and solved by the program. After the constraint solving, the input parameters and returns of the CFG are evaluated to test solution generation.

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
- improve general data structures
- implement assertive tests

## Used Open Source Libraries
- [javaparser](https://github.com/javaparser/javaparser) - Used to parse source code and code generation
- [choco-solver](https://github.com/chocoteam/choco-solver/) - Used to solve constraints and general constraint programming
- [JUnit 5](https://junit.org/junit5/) - Tests


