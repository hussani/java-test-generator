package parser;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

class ParserTest {

/*
    @Test void parseSum() throws FileNotFoundException {
        String filename = "Sum.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        Parser parser = new Parser();
        parser.parse(file);
    }
*/

    @Test void parseIfElseSimple() throws FileNotFoundException {
        String filename = "IfElseSimple.java";

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(filename).getFile());

        Parser parser = new Parser();
        parser.parse(file);
    }
}
