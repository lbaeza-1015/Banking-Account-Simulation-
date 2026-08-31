package Exceptions;

/*
 * AccountNotFoundException.java
 * Thrown when a lookup by account ID or customer ID returns no result.
 */
public class AccountNotFoundException extends BankException {
    /** Builds an error message that includes the ID that was not found. */
    public AccountNotFoundException(String accountId) {
        super("Account not found: " + accountId);
    }
}
