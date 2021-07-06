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

import java.util.Hashtable;

public class TestTemplate {
    public ClassOrInterfaceDeclaration testBase(String className) {
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

    public void testCreateTestMethod(CFG cfg, Hashtable<String, Object> inputVars, Object expectedReturn) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.addAnnotation("Test");
        methodDeclaration.addModifier(Modifier.Keyword.PUBLIC);
        methodDeclaration.setName(cfg.getName());
        methodDeclaration.setType(new VoidType());

        BlockStmt methodBody = new BlockStmt();

        cfg.getParameters().stream().map(parameter -> {
            Object parameterValue = inputVars.get(parameter.getNameAsString());
            return createVariableDeclarator(parameterValue, parameter.getNameAsString(), parameter.getType());
        }).map(VariableDeclarationExpr::new).forEach(methodBody::addStatement);

        methodBody.addStatement(
                new VariableDeclarationExpr(
                        createVariableDeclarator(expectedReturn, "expected", cfg.getReturnType())));

        NodeList<Expression> callParameters = new NodeList<>();
        cfg.getParameters().forEach(parameter -> {
            callParameters.add(new NameExpr(parameter.getNameAsString()));
        });

        methodBody.addStatement(this.getInstanceDeclaration(cfg));

        MethodCallExpr assertExpression = new MethodCallExpr("assertEquals",
                getReturnLiteral(expectedReturn, cfg.getReturnType()),
                new MethodCallExpr(new NameExpr(getInstanceVarName(cfg)),
                        new SimpleName(cfg.getName()),
                        callParameters));
        methodBody.addStatement(assertExpression);

        methodDeclaration.setBody(methodBody);

        ClassOrInterfaceDeclaration klass = this.testBase(cfg.getClassName());
        klass.addMember(methodDeclaration);
        CompilationUnit cu = getCompilationUnit(cfg.getPackageName());
        cu.addType(klass);
        klass.getFullyQualifiedName();
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
