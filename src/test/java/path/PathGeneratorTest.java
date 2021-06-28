package path;

import graph.CFG;
import graph.CFGNode;
import graph.CGFBuilder;
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
        final List<GraphPath<CFGNode, DefaultEdge>> allPathsFromCFG = generator.getAllPathsFromCFG(cfg);

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
