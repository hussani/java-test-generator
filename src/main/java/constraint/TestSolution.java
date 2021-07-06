package constraint;

import cgf.CFG;

import java.util.Hashtable;

public class TestSolution {

    private final CFG cfg;

    private final Hashtable<String, Object> constraintsSolved;

    private final Object expectedReturn;

    public TestSolution(CFG cfg, Hashtable<String, Object> constraintsSolved, Object expectedReturn) {
        this.cfg = cfg;
        this.constraintsSolved = constraintsSolved;
        this.expectedReturn = expectedReturn;
    }

    public CFG getCfg() {
        return cfg;
    }

    public Hashtable<String, Object> getConstraintsSolved() {
        return new Hashtable<>(constraintsSolved);
    }

    public Object getExpectedReturn() {
        return expectedReturn;
    }
}
