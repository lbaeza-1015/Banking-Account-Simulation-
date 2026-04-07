package exceptions;

/*
 * InvalidAmountException.java
 * Thrown when someone tries to deposit or withdraw $0 or a negative number.
 * Acts as a basic input validation guard across all account types.
 */
public class InvalidAmountException extends BankException {
    public InvalidAmountException(double amount) {
        super("Invalid amount: $" + amount + ". Amount must be greater than zero.");
    }
}
