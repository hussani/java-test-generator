package path;

import cgf.CFG;
import cgf.CFGNode;
import cgf.CGFBuilder;
import graph.CustomEdge;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultEdge;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PathGeneratorTest {

    @Test
    public void getAllPathsFromCFGShouldReturnTheCorrectNumberOfPaths() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<CFGNode, CustomEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

        assertEquals(2, allPathsFromCFG.size());
    }

    @Test
    public void getAllPathsFromCFGShouldReturnTheCorrectNumberOfPathsUsingId() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final CFG cfg = builder.buildCFGFromFile(file).get(0);

        PathGenerator generator = new PathGenerator();
        final List<GraphPath<String, DefaultEdge>> allPathsFromCFG = generator.getAllPathsWithIdsFromCFG(cfg);

        assertEquals(2, allPathsFromCFG.size());
    }
}
