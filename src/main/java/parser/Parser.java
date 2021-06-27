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
import graph.CFGEndNode;
import graph.CFGNode;
import graph.CFGRootNode;
import graph.CFGSimpleNode;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    private CFGSimpleNode lastBranch;

    public void parse(final File file) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(file);
        final List<CFG> cfgList = cu.findAll(MethodDeclaration.class)
                                    .stream()
                                    .map(this::getPaths)
                                    .collect(Collectors.toList());

        System.out.println(Arrays.toString(cfgList.toArray()));
    }

    private CFG getPaths(MethodDeclaration node) {
        final CFG cfg = this.getPaths(node, new CFG(), null);
        final CFGEndNode end = new CFGEndNode();

        final List<CFGNode> fromNodes = cfg.getEdges()
                                                   .values()
                                                   .stream()
                                                   .map(CFGEdge::getFrom)
                                                   .collect(Collectors.toList());

        final List<CFGNode> leafNodes = cfg.getNodes()
                                                   .values()
                                                   .stream()
                                                   .filter(entryNode -> !fromNodes.contains(entryNode))
                                                   .collect(Collectors.toList());
        cfg.addNode(end);
        leafNodes.forEach(leaf -> cfg.addEdge(new CFGEdge(leaf, end)));

        return cfg;
    }

    private CFG getPaths(Node node, CFG cfg, CFGNode parent) {
        System.out.println("Printing " + node.getClass().getName() + ". Range: " + node.getRange().get());
        if (node instanceof MethodDeclaration) {
            ((MethodDeclaration) node).getBody().ifPresent(body -> {
                System.out.println(body);
                final CFGNode root = new CFGRootNode();
                cfg.addNode(root);
                this.getPaths(body, cfg, root);
            });
            return cfg;
        }

        if (this.lastBranch != null && this.lastBranch != parent) {
            parent = this.lastBranch;
            this.lastBranch = null;
        }

        final CFGSimpleNode currentCFGSimpleNode = new CFGSimpleNode(Collections.singletonList(node));

        if (node instanceof BlockStmt) {
            CFGNode finalParent = parent;
            ((BlockStmt) node).getStatements().forEach(statement -> getPaths(statement, cfg, finalParent));
            return cfg;
        }

        if (node instanceof IfStmt) {
            final Expression condition = ((IfStmt) node).getCondition();
            System.out.println("If Expression. Condition found: " + condition.toString());
            this.getPaths(((IfStmt) node).getThenStmt(), cfg, currentCFGSimpleNode);
            this.lastBranch = currentCFGSimpleNode;
        }

        if (node instanceof ReturnStmt && ((ReturnStmt) node).getExpression().isPresent()) {
            final Expression expression = ((ReturnStmt) node).getExpression().get();
            System.out.println("Return Expression: " + expression.getClass().getName() + " " + expression.toString());
        }

        cfg.addNode(currentCFGSimpleNode);

        if (parent != null) {
            cfg.addEdge(new CFGEdge(parent, currentCFGSimpleNode));
        }
        return cfg;
    }
}


