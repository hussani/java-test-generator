package cgf;

import com.github.javaparser.ast.Node;

import java.util.Optional;

public abstract class CFGNode {

    public abstract String getId();

    public Optional<Node> getAstNode() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return this.getId();
    }
}
