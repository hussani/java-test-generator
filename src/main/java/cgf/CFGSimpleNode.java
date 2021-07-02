package cgf;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

import java.util.Optional;
import java.util.stream.Collectors;

public class CFGSimpleNode extends CFGNode {

    private final Node astNode;

    public CFGSimpleNode(Node astNode) {
        this.astNode = astNode;
    }

    @Override
    public Optional<Node> getAstNode() {
        return Optional.of(astNode);
    }

    @Override
    public String getId() {
        return this.astNode.getRange().stream().map(Range::toString).collect(Collectors.joining());
    }
}
