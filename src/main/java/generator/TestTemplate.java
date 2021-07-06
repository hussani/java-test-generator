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
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.VoidType;

import java.util.Hashtable;

public class TestTemplate {
    public ClassOrInterfaceDeclaration testBase() {
        ClassOrInterfaceDeclaration klass = new ClassOrInterfaceDeclaration();
        klass.setName("FileTest");
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

    public void testCreateTestMethod(CFG cfg, Hashtable<String, Object> inputVars) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.addAnnotation("Test");
        methodDeclaration.addModifier(Modifier.Keyword.PUBLIC);
        methodDeclaration.setName(cfg.getName());
        methodDeclaration.setType(new VoidType());

        BlockStmt methodBody = new BlockStmt();

        cfg.getParameters().stream().map(parameter -> {
            VariableDeclarator declarator = new VariableDeclarator(parameter.getType(), parameter.getName());
            Object parameterValue = inputVars.get(parameter.getNameAsString());
            if (parameter.getType().equals(PrimitiveType.booleanType())) {
                declarator.setInitializer(new BooleanLiteralExpr((boolean) parameterValue));
            }
            if (parameter.getType().equals(PrimitiveType.intType())) {
                declarator.setInitializer(new IntegerLiteralExpr(parameterValue.toString()));
            }
            return declarator;
        }).map(VariableDeclarationExpr::new).forEach(methodBody::addStatement);

        methodDeclaration.setBody(methodBody);

        ClassOrInterfaceDeclaration klass = this.testBase();
        klass.addMember(methodDeclaration);
        CompilationUnit cu = getCompilationUnit("testPackage");
        cu.addType(klass);
        klass.getFullyQualifiedName();
    }

    private CompilationUnit getCompilationUnit(String packageName) {
        CompilationUnit cu = new CompilationUnit();
        cu.setImports(getImports());
        cu.setPackageDeclaration(packageName);

        return cu;
    }
}
