package graph;

import com.github.javaparser.Range;
import com.github.javaparser.ast.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CFGNode {
    private List<Node> statements = new ArrayList<>();

    public CFGNode(List<Node> statements) {
        this.statements = statements;
    }

    public CFGNode() {
        // empty constructor
    }

    public List<Node> getStatements() {
        return statements;
    }

    public String getId() {
        if (this.statements.isEmpty()) {
            return "root";
        }

        final String firstStatementRange =
                this.statements.get(0).getRange().stream().map(Range::toString).collect(Collectors.joining());

        if (this.statements.size() == 1) {
            return firstStatementRange;
        }

        final String lastStatementRange = this.statements.get(this.statements.size() - 1)
                                                         .getRange()
                                                         .stream()
                                                         .map(Range::toString)
                                                         .collect(Collectors.joining());

        return firstStatementRange + "-" + lastStatementRange;
    }
}
