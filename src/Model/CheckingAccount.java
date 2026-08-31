package Model;

/*
 * CheckingAccount.java
 * Extends Account. Adds an overdraftLimit, meaning the balance can go negative
 * up to that limit before an OverdraftException is thrown.
 * Fires a Notifiable alert if the balance goes negative after a withdrawal.
 * Implements Transactable and Notifiable.
 */
import Exceptions.InsufficientFundsException;
import Exceptions.InvalidAmountException;
import Exceptions.OverdraftException;
import Interfaces.Notifiable;
import Interfaces.Transactable;
import java.io.Serializable;

public class CheckingAccount extends Account implements Transactable, Notifiable, Serializable {
    private final double overdraftLimit;

    private static int txCounter = 1;

    /** Creates a checking account with an overdraft safety net up to overdraftLimit dollars. */
    public CheckingAccount(String accountId, Customer owner,
                           double initialBalance, double overdraftLimit) {
        super(accountId, owner, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    /** Adds the given amount to the balance and records a DEPOSIT transaction. */
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        balance += amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.DEPOSIT, amount, "Deposit"));
    }

    /**
     * Deducts the given amount, allowing the balance to go negative up to overdraftLimit.
     * Fires a low-balance alert if the withdrawal pushes the balance below zero.
     */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        // The floor is -overdraftLimit; going below it means the withdrawal is refused
        if (balance - amount < -overdraftLimit)
            throw new OverdraftException(accountId, overdraftLimit);
        balance -= amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, amount, "Withdrawal"));
        if (balance < 0) sendAlert("Balance is negative: $" + String.format("%.2f", balance));
    }

    /** Prints an alert message to stdout prefixed with the account ID. */
    @Override
    public void sendAlert(String message) {
        System.out.println("[ALERT][" + accountId + "] " + message);
    }

    @Override
    public AccountType getAccountType() { return AccountType.CHECKING; }

    @Override
    public String getSummary() {
        return "CheckingAccount[" + accountId + "] Owner: " + owner.getName()
                + " | Balance: $" + String.format("%.2f", balance)
                + " | Overdraft Limit: $" + overdraftLimit;
    }

    public double getOverdraftLimit() { return overdraftLimit; }
}
