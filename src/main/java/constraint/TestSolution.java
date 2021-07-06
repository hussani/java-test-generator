package constraint;

import java.util.Hashtable;

public class TestSolution {

    private Hashtable<String, Object> constraintsSolved;

    private Object expectedReturn;

    public TestSolution(Hashtable<String, Object> constraintsSolved, Object expectedReturn) {
        this.constraintsSolved = constraintsSolved;
        this.expectedReturn = expectedReturn;
    }

    public Hashtable<String, Object> getConstraintsSolved() {
        return new Hashtable<>(constraintsSolved);
    }

    public Object getExpectedReturn() {
        return expectedReturn;
    }
}
