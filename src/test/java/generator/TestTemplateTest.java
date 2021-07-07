package generator;

import cgf.CFG;
import cgf.CFGNode;
import cgf.CFGBuilder;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import constraint.ConstraintSolver;
import constraint.TestSolution;
import graph.CustomEdge;
import org.jgrapht.GraphPath;
import org.junit.jupiter.api.Test;
import path.PathGenerator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestTemplateTest {

    @Test
    public void testGenerateTestFile() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());

        CFGBuilder builder = new CFGBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        final List<TestSolution> testSolutions = allPathsFromCFG.stream().map(path -> {
            ConstraintSolver solver = new ConstraintSolver(cfg, path);
            return new TestSolution(cfg, solver.solveConstraints(), solver.resolveExpectedReturn());
        }).collect(Collectors.toList());

        TestTemplate template = new TestTemplate();

        CompilationUnit cu = template.createCompilationUnit(cfg.getClassName(), testSolutions);

        assertEquals(2, cu.findAll(MethodDeclaration.class).size());
    }

    @Test
    public void testGenerateComplexIfChainTestFile() throws FileNotFoundException {
        String filename = "If4Paths.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());

        CFGBuilder builder = new CFGBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        final List<TestSolution> testSolutions = allPathsFromCFG.stream().map(path -> {
            ConstraintSolver solver = new ConstraintSolver(cfg, path);
            return new TestSolution(cfg, solver.solveConstraints(), solver.resolveExpectedReturn());
        }).collect(Collectors.toList());

        TestTemplate template = new TestTemplate();

        CompilationUnit cu = template.createCompilationUnit(cfg.getClassName(), testSolutions);

        assertEquals(4, cu.findAll(MethodDeclaration.class).size());
    }

    @Test
    public void testGenerateComplexTestFile() throws FileNotFoundException {
        String filename = "Sum.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource(filename)).getFile());

        CFGBuilder builder = new CFGBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        final List<TestSolution> testSolutions = allPathsFromCFG.stream().map(path -> {
            ConstraintSolver solver = new ConstraintSolver(cfg, path);
            return new TestSolution(cfg, solver.solveConstraints(), solver.resolveExpectedReturn());
        }).collect(Collectors.toList());

        TestTemplate template = new TestTemplate();

        CompilationUnit cu = template.createCompilationUnit(cfg.getClassName(), testSolutions);

        assertEquals(1, cu.findAll(MethodDeclaration.class).size());
    }
}
