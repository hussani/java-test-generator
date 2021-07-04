package constraint;

import cgf.CFG;
import cgf.CFGNode;
import cgf.CFGSimpleNode;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.nodeTypes.NodeWithCondition;
import com.github.javaparser.ast.type.PrimitiveType;
import graph.CustomEdge;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.Variable;
import org.jgrapht.GraphPath;

import java.util.Hashtable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConstraintSolver {

    private static final String SOLVING_TIME_LIMIT = "10s";
    private final Model model;
    private final Hashtable<String, Variable> modelVariables = new Hashtable<>();
    private final Hashtable<String, Variable> modelConstraints = new Hashtable<>();

    public ConstraintSolver() {
        this.model = new Model();
    }

    public Hashtable<String, Parameter> solveConstraints(CFG cfg, GraphPath<CFGNode, CustomEdge> graphPath) {
        Hashtable<String, Variable> variables = new Hashtable<>();
        cfg.getParameters().forEach(p -> modelVariables.put(p.getNameAsString(), this.astParameterToModelVar(p)));

        final List<Expression> expressions = getExpressions(graphPath);

        expressions.stream()
                   .filter(Expression::isBinaryExpr)
                   .map(Expression::asBinaryExpr)
                   .forEach(this::expressionToConstraint);

        Solver solver = model.getSolver();
        solver.limitTime(SOLVING_TIME_LIMIT);
        Solution solution = new Solution(model);
        if (solver.solve()) {
            solution.record();
        }

        return null;
    }

    private void expressionToConstraint(BinaryExpr expression) {
        if (!expression.getLeft().isNameExpr() || !expression.getRight().isNameExpr()) {
            return;
        }
        Variable left = modelVariables.get(expression.getLeft().asNameExpr().getNameAsString());
        Variable right =  modelVariables.get(expression.getRight().asNameExpr().getNameAsString());

        model.arithm(left.asIntVar(), expression.getOperator().asString(), right.asIntVar()).post();

    }

    private List<Expression> getExpressions(GraphPath<CFGNode, CustomEdge> graphPath) {
        return graphPath.getEdgeList()
                        .stream()
                        .map(customEdge -> (CFGNode) customEdge.getSource())
                        .filter(cfgNode -> cfgNode instanceof CFGSimpleNode)
                        .map(CFGNode::getAstNode)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(node -> node instanceof NodeWithCondition)
                        .map((Node node) -> ((NodeWithCondition<?>) node).getCondition())
                        .collect(Collectors.toList());
    }

    private Variable astParameterToModelVar(Parameter parameter) {
        if (!parameter.getType().isPrimitiveType()) {
            return null;
        }

        PrimitiveType type = (PrimitiveType) parameter.getType();

        if (type.getType().equals(PrimitiveType.Primitive.BOOLEAN)) {
            return model.boolVar(parameter.getName().asString());
        }

        if (type.getType().equals(PrimitiveType.Primitive.INT)) {
            return model.intVar(parameter.getName().asString(), -1000, 1000);
        }

        return null;
    }
}
