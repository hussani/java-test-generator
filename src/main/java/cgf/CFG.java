package cgf;

import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class CFG {
    private Hashtable<String, CFGNode> nodes;
    private Hashtable<String, CFGEdge> edges;
    private String name;
    private String className;
    private String fullQualifiedName;
    private String packageName;
    private Type returnType;
    private final List<Parameter> parameters;

    public CFG() {
        this.nodes = new Hashtable<>();
        this.edges = new Hashtable<>();
        this.parameters = new ArrayList<>();
    }

    public Hashtable<String, CFGNode> getNodes() {
        return nodes;
    }

    public void setNodes(Hashtable<String, CFGNode> nodes) {
        this.nodes = nodes;
    }

    public Hashtable<String, CFGEdge> getEdges() {
        return edges;
    }

    public void setEdges(Hashtable<String, CFGEdge> edges) {
        this.edges = edges;
    }

    public void addNode(CFGNode node) {
        this.nodes.put(node.getId(), node);
    }

    public void addEdge(CFGEdge edges) {
        this.edges.put(edges.getId(), edges);
    }

    public CFGNode getRootNode() {
        return this.nodes.get("root");
    }

    public CFGNode getEndNode() {
        return this.nodes.get("end");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFullQualifiedName() {
        return fullQualifiedName;
    }

    public void setFullQualifiedName(String fullQualifiedName) {
        this.fullQualifiedName = fullQualifiedName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
