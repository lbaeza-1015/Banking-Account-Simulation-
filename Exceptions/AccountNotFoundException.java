package Exceptions;

public class AccountNotFoundException extends BankException {
    public AccountNotFoundException(String accountId) {
        super("Account not found: " + accountId);
    }
}
