package graph;

import java.util.Hashtable;

public class CFG {
    private Hashtable<String, CFGNode> nodes;
    private Hashtable<String, CFGEdge> edges;

    public CFG() {
        this.nodes = new Hashtable<String, CFGNode>();
        this.edges = new Hashtable<String, CFGEdge>();
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
}
