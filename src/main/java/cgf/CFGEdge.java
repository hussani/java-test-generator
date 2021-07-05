package cgf;

public class CFGEdge {

    private final CFGNode from;
    private final CFGNode to;
    private final Boolean forkNegate;

    public CFGEdge(CFGNode from, CFGNode to, Boolean forkNegate) {
        this.from = from;
        this.to = to;
        this.forkNegate = forkNegate;
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

    public Boolean getForkNegate() {
        return forkNegate;
    }
}
