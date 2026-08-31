package Model;

/*
 * SavingsAccount.java
 * Extends Account. Adds an interestRate and minimumBalance.
 * Withdrawals are blocked if they would drop the balance below the minimum.
 * applyInterest() credits interest directly to the balance.
 * Implements Transactable, InterestBearing, and Notifiable.
 */
import Exceptions.InsufficientFundsException;
import Exceptions.InvalidAmountException;
import Interfaces.InterestBearing;
import Interfaces.Notifiable;
import Interfaces.Transactable;
import java.io.Serializable;

public class SavingsAccount extends Account implements Transactable, InterestBearing, Notifiable, Serializable {
    private double interestRate;
    private final double minimumBalance;

    private static int txCounter = 1;

    /** Creates a savings account with an interest rate and a minimum required balance. */
    public SavingsAccount(String accountId, Customer owner,
                          double initialBalance, double interestRate, double minimumBalance) {
        super(accountId, owner, initialBalance);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
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
     * Deducts the given amount from the balance, enforcing the minimum balance requirement.
     * The effective withdrawal ceiling is (balance - minimumBalance), not the full balance.
     */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        // balance - minimumBalance is the most the customer can actually withdraw
        if (balance - amount < minimumBalance)
            throw new InsufficientFundsException(accountId, amount, balance - minimumBalance);
        balance -= amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, amount, "Withdrawal"));
    }

    /** Returns the interest earned this period (balance × rate). */
    @Override
    public double calculateInterest() {
        return balance * interestRate;
    }

    /** Calculates and credits interest to the balance, then records an INTEREST_APPLIED transaction. */
    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        balance += interest;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.INTEREST_APPLIED, interest, "Interest applied"));
    }

    /** Reverses the last interest application by removing balance * rate / (1 + rate) from the balance. */
    @Override
    public void removeInterest() {
        double interest = balance * interestRate / (1 + interestRate);
        balance -= interest;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.INTEREST_REMOVED, interest, "Interest removed"));
    }

    /** Prints an alert message to stdout prefixed with the account ID. */
    @Override
    public void sendAlert(String message) {
        System.out.println("[ALERT][" + accountId + "] " + message);
    }

    @Override
    public AccountType getAccountType() { return AccountType.SAVINGS; }

    @Override
    public String getSummary() {
        return "SavingsAccount[" + accountId + "] Owner: " + owner.getName()
                + " | Balance: $" + String.format("%.2f", balance)
                + " | Rate: " + (interestRate * 100) + "%"
                + " | Min Balance: $" + minimumBalance;
    }

    public double getInterestRate()  { return interestRate; }
    public double getMinimumBalance() { return minimumBalance; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }
}
