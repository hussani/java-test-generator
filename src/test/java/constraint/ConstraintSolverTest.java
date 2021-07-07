package constraint;

import cgf.CFG;
import cgf.CFGNode;
import cgf.CFGBuilder;
import graph.CustomEdge;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.Test;
import path.PathGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstraintSolverTest {

    @Test
    public void solveConstraintsShouldReturnTheCorrectNumberOfParametersSolved() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());

        CFGBuilder builder = new CFGBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        ConstraintSolver solver = new ConstraintSolver(cfg, allPathsFromCFG.get(0));
        final Hashtable<String, Object> constraintsSolved = solver.solveConstraints();

        assertEquals(2, constraintsSolved.size());
    }

    @Test
    public void resolveReturnShouldReturnTheCorrectExpectedReturnWithoutSolve() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());

        CFGBuilder builder = new CFGBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        ConstraintSolver solver = new ConstraintSolver(cfg, allPathsFromCFG.get(0));
        solver.solveConstraints();

        assertEquals(1, solver.resolveExpectedReturn());
    }
}
