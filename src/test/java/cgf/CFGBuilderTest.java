package cgf;

import graph.CFG;
import graph.CGFBuilder;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CFGBuilderTest {

    @Test void buildIfElseSimple() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        CGFBuilder builder = new CGFBuilder();
        final List<CFG> cfgList = builder.buildCFGFromFile(file);

        assertEquals(1, cfgList.size());
        assertEquals(5, cfgList.get(0).getEdges().size());
        assertEquals(5, cfgList.get(0).getNodes().size());
    }
}
