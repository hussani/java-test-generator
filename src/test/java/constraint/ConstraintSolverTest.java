package constraint;

import cgf.CFG;
import cgf.CFGNode;
import cgf.CGFBuilder;
import graph.CustomEdge;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.Test;
import path.PathGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Hashtable;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ConstraintSolverTest {

    @Test
    public void solveConstraintsShouldReturnTheCorrectNumberOfParametersSolved() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
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
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        ConstraintSolver solver = new ConstraintSolver(cfg, allPathsFromCFG.get(0));
        final Hashtable<String, Object> constraintsSolved = solver.solveConstraints();

        assertEquals(1, solver.resolveExpectedReturn());
    }
}
