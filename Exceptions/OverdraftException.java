package Exceptions;

/*
 * OverdraftException.java
 * Thrown specifically on CheckingAccount when a withdrawal would push
 * the balance past the allowed overdraft limit.
 */
public class OverdraftException extends BankException {
    public OverdraftException(String accountId, double overdraftLimit) {
        super("Overdraft limit exceeded on account " + accountId +
              ". Limit is $" + overdraftLimit);
    }
}
