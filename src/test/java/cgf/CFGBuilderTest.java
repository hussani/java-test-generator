package cgf;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CFGBuilderTest {

    @Test
    public void buildIfElseSimple() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final List<CFG> cfgList = builder.buildCFGFromFile(file);

        assertEquals(1, cfgList.size());
        assertEquals(5, cfgList.get(0).getEdges().size());
        assertEquals(5, cfgList.get(0).getNodes().size());
    }

    @Test
    public void buildSum() throws FileNotFoundException {
        String filename = "Sum.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final List<CFG> cfgList = builder.buildCFGFromFile(file);

        assertEquals(1, cfgList.size());
        assertEquals(2, cfgList.get(0).getEdges().size());
        assertEquals(3, cfgList.get(0).getNodes().size());
    }

    @Test
    public void cfgShouldNotHaveForkBranches() throws FileNotFoundException {
        String filename = "Sum.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final List<CFG> cfgList = builder.buildCFGFromFile(file);

        assertEquals(1, cfgList.size());
        assertEquals(2, cfgList.get(0).getEdges().size());
        cfgList.forEach(cfg -> cfg.getEdges().values().forEach(cfgEdge -> assertFalse(cfgEdge.getForkNegate())));
    }

    @Test
    public void cfgShouldHaveForkBranches() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final List<CFG> cfgList = builder.buildCFGFromFile(file);

        cfgList.stream().map(cfg -> {
            return cfg.getEdges().values().stream().filter(CFGEdge::getForkNegate).collect(Collectors.toList());
        }).forEach(edgesWithFork -> assertEquals(1, edgesWithFork.size()));

        assertEquals(1, cfgList.size());
        assertEquals(5, cfgList.get(0).getEdges().size());
        assertEquals(5, cfgList.get(0).getNodes().size());
    }
}
