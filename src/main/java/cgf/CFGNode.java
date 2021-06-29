package cgf;

public abstract class CFGNode {

    public abstract String getId();

    @Override
    public String toString() {
        return this.getId();
    }
}
