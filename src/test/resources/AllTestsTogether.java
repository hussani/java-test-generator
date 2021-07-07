package parser;

public class AllTestsTogether {

    public int compare(int operator1, int operator2, int operator3, int operator4) {
        if (operator1 > operator2) {
            return 1;
        }

        if (operator3 > operator4) {
            return 2;
        }

        if (operator4 > operator1) {
            return 3;
        }

        return 4;
    }

    public int compare(int operator1, int operator2) {
        if (operator1 > operator2) {
            return 1;
        }
        return 2;
    }

    public int sum(int operator1, int operator2) {
        return operator1 + operator2;
    }
}
