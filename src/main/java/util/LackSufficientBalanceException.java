package util;

public class LackSufficientBalanceException extends Exception{

    public LackSufficientBalanceException(String message) {
        super(message);
    }
}
