package Model;

/*
 * CheckingAccount.java
 * Extends Account. Adds an overdraftLimit, meaning the balance can go negative
 * up to that limit before an OverdraftException is thrown.
 * Fires a Notifiable alert if the balance goes negative after a withdrawal.
 * Implements Transactable and Notifiable.
 */
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import exceptions.OverdraftException;
import interfaces.Notifiable;
import interfaces.Transactable;

public class CheckingAccount extends Account implements Transactable, Notifiable {
    private final double overdraftLimit;

    private static int txCounter = 1;

    public CheckingAccount(String accountId, Customer owner,
                           double initialBalance, double overdraftLimit) {
        super(accountId, owner, initialBalance);
        this.overdraftLimit = overdraftLimit;
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        balance += amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.DEPOSIT, amount, "Deposit"));
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (balance - amount < -overdraftLimit)
            throw new OverdraftException(accountId, overdraftLimit);
        balance -= amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, amount, "Withdrawal"));
        if (balance < 0) sendAlert("Balance is negative: $" + String.format("%.2f", balance));
    }

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
