package generator;

import cgf.CFG;
import cgf.CFGNode;
import cgf.CGFBuilder;
import constraint.ConstraintSolver;
import constraint.TestSolution;
import graph.CustomEdge;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.Test;
import path.PathGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

class TestTemplateTest {

    @Test
    public void testGenerateTestFile() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        final List<TestSolution> collect = allPathsFromCFG.stream().map(path -> {
            ConstraintSolver solver = new ConstraintSolver(cfg, path);
            return new TestSolution(solver.solveConstraints(), solver.resolveExpectedReturn());
        }).collect(Collectors.toList());

        TestTemplate template = new TestTemplate();

        template.createCompilationUnit(cfg, collect);
    }
}
