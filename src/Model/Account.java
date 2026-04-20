package Model;

/*
 * Account.java
 * The abstract superclass for all account types.
 * Holds the shared fields every account has: accountId, balance, owner, and history.
 * Forces all subclasses to implement getAccountType() and getSummary(),
 * since those answers differ per account type.
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Serializable {
    protected final String accountId;
    protected double balance;
    protected final Customer owner;
    protected final List<Transaction> history;

    public Account(String accountId, Customer owner, double initialBalance) {
        this.accountId = accountId;
        this.owner = owner;
        this.balance = initialBalance;
        this.history = new ArrayList<>();
    }

    public String getAccountId()        { return accountId; }
    public double getBalance()          { return balance; }
    public Customer getOwner()          { return owner; }
    public List<Transaction> getHistory() { return history; }

    public abstract AccountType getAccountType();
    public abstract String getSummary();

    protected void recordTransaction(Transaction t) {
        history.add(t);
    }

    @Override
    public String toString() {
        return getAccountType() + " Account [" + accountId + "] Balance: $"
                + String.format("%.2f", balance);
    }
}
