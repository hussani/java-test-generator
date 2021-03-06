package cgf;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

public class CFGBuilder {

    private CFGSimpleNode lastBranch;

    public List<CFG> buildCFGFromFile(final File file) throws FileNotFoundException {
        CompilationUnit cu = StaticJavaParser.parse(file);
        return cu.findAll(MethodDeclaration.class).stream()
                 .map(this::buildCFGFromMethodDeclaration)
                 .peek(cfg -> {
                     String packageName = cu.getPackageDeclaration().map(NodeWithName::getNameAsString).orElse("");
                     cfg.setPackageName(packageName);
                 }).collect(Collectors.toList());
    }

    private CFG buildCFGFromMethodDeclaration(MethodDeclaration node) {
        final CFG cfg = this.introspectCFG(node, new CFG(), null);
        final CFGEndNode end = new CFGEndNode();
        final ClassOrInterfaceDeclaration parentClass = (ClassOrInterfaceDeclaration) node.getParentNode().get();

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
        leafNodes.forEach(leaf -> cfg.addEdge(new CFGEdge(leaf, end, false)));

        cfg.setReturnType(node.getType());
        cfg.setName(node.getName().getIdentifier());
        cfg.setClassName(parentClass.getNameAsString());
        cfg.setFullQualifiedName(parentClass.getFullyQualifiedName().orElse(""));
        node.getParameters().forEach(cfg::addParameter);

        return cfg;
    }

    private CFG introspectCFG(Node node, CFG cfg, CFGNode parent) {
        if (node instanceof MethodDeclaration) {
            ((MethodDeclaration) node).getBody().ifPresent(body -> {
                final CFGNode root = new CFGRootNode();
                cfg.addNode(root);
                this.introspectCFG(body, cfg, root);
            });
            return cfg;
        }

        boolean forkRightSide = false;

        if (this.lastBranch != null && this.lastBranch != parent) {
            parent = this.lastBranch;
            this.lastBranch = null;
            forkRightSide = true;
        }

        final CFGSimpleNode currentCFGSimpleNode = new CFGSimpleNode(node);

        if (node instanceof BlockStmt) {
            CFGNode finalParent = parent;
            ((BlockStmt) node).getStatements().forEach(statement -> introspectCFG(statement, cfg, finalParent));
            return cfg;
        }

        if (node instanceof IfStmt) {
            final Expression condition = ((IfStmt) node).getCondition();
            this.introspectCFG(((IfStmt) node).getThenStmt(), cfg, currentCFGSimpleNode);
            this.lastBranch = currentCFGSimpleNode;
        }

        if (node instanceof ReturnStmt && ((ReturnStmt) node).getExpression().isPresent()) {
            final Expression expression = ((ReturnStmt) node).getExpression().get();
        }

        cfg.addNode(currentCFGSimpleNode);

        if (parent != null) {
            cfg.addEdge(new CFGEdge(parent, currentCFGSimpleNode, forkRightSide));
        }
        return cfg;
    }
}
