package constraint;

import cgf.CFG;
import cgf.CFGEdge;
import cgf.CFGNode;
import cgf.CFGSimpleNode;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithCondition;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import graph.CustomEdge;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Operator;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.ESat;
import org.jgrapht.GraphPath;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ConstraintSolver {

    private static final String SOLVING_TIME_LIMIT = "10s";
    public static final int LOW_BOUNDARY = -1000;
    public static final int UP_BOUNDARY = 1000;
    private final Model model;
    private final Hashtable<String, Variable> modelVariables = new Hashtable<>();
    private final CFG cfg;
    private final GraphPath<CFGNode, CustomEdge> graphPath;
    private boolean returnIsLiteral;
    private Object solvedReturn;
    private Variable returnConstraintVar;

    public ConstraintSolver(final CFG cfg, final GraphPath<CFGNode, CustomEdge> graphPath) {
        this.model = new Model();
        this.cfg = cfg;
        this.graphPath = graphPath;
    }

    public Hashtable<String, Object> solveConstraints() {
        cfg.getParameters().forEach(p -> modelVariables.put(p.getNameAsString(), this.astParameterToModelVar(p)));

        getExpressions(graphPath);
        evaluateReturnExpression();

        Solver solver = model.getSolver();
        solver.limitTime(SOLVING_TIME_LIMIT);
        Solution solution = new Solution(model);

        if (solver.solve()) {
            solution.record();

            final Map<String, Object> collect = modelVariables.entrySet()
                                                              .stream()
                                                              .collect(Collectors.toMap(Map.Entry::getKey,
                                                                      this::getSolvedValue));
            if (!this.returnIsLiteral) {
                this.solvedReturn = this.getSolvedValue(this.returnConstraintVar);
            }

            return new Hashtable<>(collect);
        }

        throw new RuntimeException();
    }

    public Object resolveExpectedReturn() {
        return this.solvedReturn;
    }

    private Object getLiteralReturn(Expression expression) {
        if (expression instanceof IntegerLiteralExpr) {
            return expression.asIntegerLiteralExpr().asNumber().intValue();
        }

        if (expression instanceof BooleanLiteralExpr) {
            return expression.asBooleanLiteralExpr().getValue();
        }
        throw new RuntimeException();
    }

    private void evaluateReturnExpression() {
        final Optional<Expression> optExpression = graphPath.getEdgeList()
                                                            .stream()
                                                            .map(customEdge -> (CFGNode) customEdge.getSource())
                                                            .filter(cfgNode -> cfgNode instanceof CFGSimpleNode)
                                                            .map(CFGNode::getAstNode)
                                                            .filter(Optional::isPresent)
                                                            .map(Optional::get)
                                                            .filter(node -> node instanceof ReturnStmt)
                                                            .map((Node node) -> ((ReturnStmt) node).getExpression())
                                                            .filter(Optional::isPresent)
                                                            .map(Optional::get)
                                                            .findFirst();

        if (optExpression.isEmpty()) {
            throw new RuntimeException();
        }

        final Expression expression = optExpression.get();

        if (!(expression instanceof BinaryExpr)) {
            this.returnIsLiteral = true;
            this.solvedReturn = this.getLiteralReturn(expression);
            return;
        }

        Variable left, right;
        BinaryExpr binaryExpr = expression.asBinaryExpr();
        if (binaryExpr.getLeft().isNameExpr() && modelVariables.containsKey(binaryExpr.getLeft().asNameExpr().getNameAsString())) {
            left = modelVariables.get(binaryExpr.getLeft().asNameExpr().getNameAsString());
        } else {
            throw new RuntimeException();
        }

        if (binaryExpr.getRight().isNameExpr() && modelVariables.containsKey(binaryExpr.getRight().asNameExpr().getNameAsString())) {
            right = modelVariables.get(binaryExpr.getRight().asNameExpr().getNameAsString());
        } else {
            throw new RuntimeException();
        }

        this.returnConstraintVar = model.intVar("return", LOW_BOUNDARY, UP_BOUNDARY);

        model.arithm(left.asIntVar(), binaryExpr.getOperator().asString(), right.asIntVar(), "=", this.returnConstraintVar.asIntVar()).post();
        this.returnIsLiteral = false;
    }

    private Object getSolvedValue(Map.Entry<String, Variable> entry) {
        Variable variable = entry.getValue();
        return getSolvedValue(variable);
    }

    private Object getSolvedValue(Variable variable) {
        if (variable.getTypeAndKind() == 9) {
            return variable.asIntVar().getValue();
        }
        if (variable.getTypeAndKind() == 25) {
            return variable.asBoolVar().getBooleanValue() == ESat.TRUE;
        }
        throw new RuntimeException();
    }

    private void expressionToConstraint(BinaryExpr expression, boolean oppositeConstraint) {
        if (!expression.getLeft().isNameExpr() || !expression.getRight().isNameExpr()) {
            return;
        }
        Variable left = modelVariables.get(expression.getLeft().asNameExpr().getNameAsString());
        Variable right =  modelVariables.get(expression.getRight().asNameExpr().getNameAsString());

        Operator operator = Operator.get(expression.getOperator().asString());

        if (oppositeConstraint) {
            operator = Operator.getOpposite(operator);
        }

        model.arithm(left.asIntVar(), String.valueOf(operator), right.asIntVar()).post();
    }

    private void getExpressions(GraphPath<CFGNode, CustomEdge> graphPath) {

        for (CustomEdge customEdge : graphPath.getEdgeList()) {
            CFGNode cfgNode = (CFGNode) customEdge.getSource();
            if (cfgNode instanceof CFGSimpleNode) {
                Optional<Node> astNode = cfgNode.getAstNode();
                if (astNode.isPresent()) {
                    Node node = astNode.get();
                    if (node instanceof NodeWithCondition) {
                        Expression condition = ((NodeWithCondition<?>) node).getCondition();
                        if (condition.isBinaryExpr()) {
                            BinaryExpr binaryExpr = condition.asBinaryExpr();
                            expressionToConstraint(binaryExpr, getCfgEdge(cfg, customEdge).getForkNegate());
                        }
                    }
                }
            }
        }
    }

    private CFGEdge getCfgEdge(CFG cfg, CustomEdge customEdge) {
        return cfg.getEdges().get(customEdge.getSource() + "|" + customEdge.getTarget());
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
            return model.intVar(parameter.getName().asString(), LOW_BOUNDARY, UP_BOUNDARY);
        }

        return null;
    }
}
