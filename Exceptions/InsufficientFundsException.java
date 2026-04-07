package exceptions;

/*
 * InsufficientFundsException.java
 * Thrown when a withdrawal exceeds the available balance on an account
 * that does not support overdraft (e.g. SavingsAccount).
 */
public class InsufficientFundsException extends BankException {
    public InsufficientFundsException(String accountId, double requested, double available) {
        super("Insufficient funds on account " + accountId +
              ": requested $" + requested + ", available $" + available);
    }
}
