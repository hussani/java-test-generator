package parser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import graph.CFG;
import graph.CFGEdge;
import graph.CFGNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    public void parse(final File file) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(file);
        final List<CFG> cfgList = cu.findAll(MethodDeclaration.class)
                                    .stream()
                                    .map(this::getPaths)
                                    .collect(Collectors.toList());

        System.out.println(Arrays.toString(cfgList.toArray()));
    }

    private CFG getPaths(MethodDeclaration node) {
        return this.getPaths(node, new CFG(), null);
    }

    private CFG getPaths(Node node, CFG cfg, CFGNode parent) {
        System.out.println("Printing " + node.getClass().getName() + ". Range: " + node.getRange().get());
        if (node instanceof MethodDeclaration) {
            ((MethodDeclaration) node).getBody().ifPresent(body -> {
                System.out.println(body);
                this.getPaths(body, cfg, new CFGNode());
            });
        }

        final CFGNode currentCFGNode = new CFGNode(Collections.singletonList(node));

        if (node instanceof BlockStmt) {
            ((BlockStmt) node).getStatements().forEach(statement -> getPaths(statement, cfg, currentCFGNode));
        }

        if (node instanceof IfStmt) {
            final Expression condition = ((IfStmt) node).getCondition();
            System.out.println("If Expression. Condition found: " + condition.toString());
            this.getPaths(((IfStmt) node).getThenStmt(), cfg, currentCFGNode);
        }

        if (node instanceof ReturnStmt && ((ReturnStmt) node).getExpression().isPresent()) {
            final Expression expression = ((ReturnStmt) node).getExpression().get();
            System.out.println("Return Expression: " + expression.getClass().getName() + " " + expression.toString());
        }

        cfg.addNode(currentCFGNode);

        if (parent != null) {
            cfg.addEdge(new CFGEdge(parent, currentCFGNode));
        }
        return cfg;
    }
}


