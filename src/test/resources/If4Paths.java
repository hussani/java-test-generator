package parser;

public class If4Paths {

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
}
