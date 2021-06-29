package cgf;

public class CFGEdge {

    private final CFGNode from;
    private final CFGNode to;

    public CFGEdge(CFGNode from, CFGNode to) {
        this.from = from;
        this.to = to;
    }

    public CFGNode getFrom() {
        return from;
    }

    public CFGNode getTo() {
        return to;
    }

    public String getId() {
        return this.from.getId() + "|" + this.to.getId();
    }
}
