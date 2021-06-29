package cgf;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

import java.util.stream.Collectors;

public class CFGSimpleNode extends CFGNode {

    private final Node astNode;

    public CFGSimpleNode(Node astNode) {
        this.astNode = astNode;
    }

    public Node getAstNode() {
        return astNode;
    }

    @Override public String getId() {
        return this.astNode.getRange().stream().map(Range::toString).collect(Collectors.joining());
    }
}
