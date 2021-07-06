package generator;

import cgf.CFG;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LiteralExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;
import constraint.TestSolution;

import java.util.List;
import java.util.stream.IntStream;

public class TestTemplate {
    private ClassOrInterfaceDeclaration createClass(String className) {
        ClassOrInterfaceDeclaration klass = new ClassOrInterfaceDeclaration();
        klass.setName(className + "Test");
        klass.addModifier(Modifier.Keyword.PUBLIC);

        return klass;
    }

    private NodeList<ImportDeclaration> getImports() {
        return new NodeList<>(
                new ImportDeclaration("org.junit.jupiter.api.Test", false, false),
                new ImportDeclaration("org.junit.jupiter.api.Assertions.assertEquals", true,
                        false)
        );
    }

    public CompilationUnit createCompilationUnit(String className, List<TestSolution> testSolutions) {
        if (testSolutions.isEmpty()) {
            throw new RuntimeException();
        }
        ClassOrInterfaceDeclaration klass = this.createClass(className);
        IntStream.range(0, testSolutions.size())
                 .mapToObj(i -> this.getMethodDeclaration(testSolutions.get(i), i))
                 .forEach(klass::addMember);

        CompilationUnit cu = getCompilationUnit(testSolutions.get(0).getCfg().getPackageName());
        cu.addType(klass);

        return cu;
    }

    private MethodDeclaration getMethodDeclaration(TestSolution testSolution, Integer testNumber) {
        final CFG cfg = testSolution.getCfg();
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.addAnnotation("Test");
        methodDeclaration.addModifier(Modifier.Keyword.PUBLIC);
        methodDeclaration.setName(cfg.getName() + testNumber.toString());
        methodDeclaration.setType(new VoidType());

        BlockStmt methodBody = new BlockStmt();

        cfg.getParameters().stream().map(parameter -> {
            Object parameterValue = testSolution.getConstraintsSolved().get(parameter.getNameAsString());
            return createVariableDeclarator(parameterValue, parameter.getNameAsString(), parameter.getType());
        }).map(VariableDeclarationExpr::new).forEach(methodBody::addStatement);

        methodBody.addStatement(
                new VariableDeclarationExpr(
                        createVariableDeclarator(testSolution.getExpectedReturn(), "expected", cfg.getReturnType())));

        NodeList<Expression> callParameters = new NodeList<>();
        cfg.getParameters().forEach(parameter -> {
            callParameters.add(new NameExpr(parameter.getNameAsString()));
        });

        methodBody.addStatement(this.getInstanceDeclaration(cfg));

        MethodCallExpr assertExpression = new MethodCallExpr("assertEquals",
                getReturnLiteral(testSolution.getExpectedReturn(), cfg.getReturnType()),
                new MethodCallExpr(new NameExpr(getInstanceVarName(cfg)),
                        new SimpleName(cfg.getName()),
                        callParameters));
        methodBody.addStatement(assertExpression);

        methodDeclaration.setBody(methodBody);
        return methodDeclaration;
    }

    private VariableDeclarationExpr getInstanceDeclaration(CFG cfg) {
        final ClassOrInterfaceType classOrInterfaceType = new ClassOrInterfaceType(null, cfg.getClassName());
        final ObjectCreationExpr initializer = new ObjectCreationExpr(
                null, classOrInterfaceType,
                new NodeList<>()
        );

        return new VariableDeclarationExpr(
                new VariableDeclarator(classOrInterfaceType, getInstanceVarName(cfg), initializer));
    }

    private String getInstanceVarName(CFG cfg) {
        return cfg.getClassName().substring(0, 1).toLowerCase() + cfg.getClassName().substring(1);
    }

    private LiteralExpr getReturnLiteral(Object value, Type type) {
        if (type.equals(PrimitiveType.booleanType())) {
            return new BooleanLiteralExpr((Boolean) value);
        }

        if (type.equals(PrimitiveType.intType())) {
            return new IntegerLiteralExpr(value.toString());
        }

        throw new RuntimeException();
    }

    private VariableDeclarator createVariableDeclarator(Object inputVar, String variableName, Type type) {
        VariableDeclarator declarator = new VariableDeclarator(type, variableName);
        declarator.setInitializer(getReturnLiteral(inputVar, type));

        return declarator;
    }

    private CompilationUnit getCompilationUnit(String packageName) {
        CompilationUnit cu = new CompilationUnit();
        cu.setImports(getImports());
        cu.setPackageDeclaration(packageName);

        return cu;
    }
}
