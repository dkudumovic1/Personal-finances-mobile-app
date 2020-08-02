package ba.unsa.etf.rma.rma20kudumovicdzeneta19_test2;

public class NumberValidator {
    public boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }

    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        }
        catch(NumberFormatException e) {
            return false;
        }
    }
}
