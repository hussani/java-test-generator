package parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.printer.DotPrinter;
import com.github.javaparser.printer.YamlPrinter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Parser {

    public void parse(final File file) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(file);

        YamlPrinter printer = new YamlPrinter(true);
        System.out.println(printer.output(cu));

        printAst(cu, file);

        VoidVisitor<Void> methodNameVisitor = new MethodNamePrinter();

        methodNameVisitor.visit(cu, null);
    }

    private static class MethodNamePrinter extends VoidVisitorAdapter<Void> {

        @Override public void visit(MethodDeclaration md, Void arg) {
            super.visit(md, arg);
            System.out.println("Method Name Printed: " + md.getName());
        }
    }

    private void printAst(CompilationUnit cu, File file) {
        DotPrinter printer = new DotPrinter(true);
        try (FileWriter fileWriter = new FileWriter(file.getName() + ".ast.dot");
                PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.print(printer.output(cu));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


