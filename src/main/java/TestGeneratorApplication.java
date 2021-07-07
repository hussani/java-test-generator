import cgf.CFG;
import cgf.CFGBuilder;
import com.github.javaparser.ast.CompilationUnit;
import constraint.ConstraintSolver;
import constraint.TestSolution;
import generator.TestTemplate;
import path.PathGenerator;
import support.Tuple2;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class TestGeneratorApplication {

    public static final String GENERATED_TESTS_FOLDER = "generated-tests";

    public static void main(String[] args) throws IOException {
        String filename = args[0];

        File file = new File(filename);

        PathGenerator generator = new PathGenerator();
        CFGBuilder builder = new CFGBuilder();

        System.out.println("Building CFGs from " + filename);
        final List<CFG> cfgList = builder.buildCFGFromFile(file);

        System.out.println("CFGs found: " + cfgList.size());
        if (cfgList.isEmpty()) {
            throw new RuntimeException("No methods was found in file");
        }

        final String className = cfgList.get(0).getClassName();

        String output;
        if (args.length > 1) {
            output = args[1];
        } else {
            output = className + "Test.java";
        }

        final List<TestSolution> testSolutions = cfgList.stream()
                                                        .map(cfg -> new Tuple2<>(cfg,
                                                                generator.getAllPathsFromCFG(cfg)))
                                                        .map(tuple -> tuple.getT2().stream().map(path -> {
                                                            ConstraintSolver solver =
                                                                    new ConstraintSolver(tuple.getT1(), path);
                                                            return new TestSolution(tuple.getT1(),
                                                                    solver.solveConstraints(),
                                                                    solver.resolveExpectedReturn());
                                                        }).collect(Collectors.toList()))
                                                        .flatMap(List::stream)
                                                        .collect(Collectors.toList());

        System.out.println("Possible Test Cases: " + testSolutions.size());
        TestTemplate template = new TestTemplate();

        System.out.println("Generating file...");
        CompilationUnit cu = template.createCompilationUnit(className, testSolutions);

        Files.createDirectories(Paths.get(GENERATED_TESTS_FOLDER));
        String outputPath = GENERATED_TESTS_FOLDER + "/" + output;

        System.out.println("Writing into " + outputPath);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        writer.write(cu.toString());
        writer.close();

        System.out.println("Done");
    }
}
