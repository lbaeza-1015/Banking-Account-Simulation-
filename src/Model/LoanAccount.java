package Model;

/*
 * LoanAccount.java
 * Extends Account. Represents money the customer owes the bank.
 * Payments reduce the remaining balance via makePayment().
 * Withdrawing is not allowed and throws UnsupportedOperationException.
 * Fires an alert when the loan is fully paid off.
 * Implements Transactable and Notifiable.
 */
import Exceptions.InsufficientFundsException;
import Exceptions.InvalidAmountException;
import Interfaces.Notifiable;
import Interfaces.Transactable;
import java.io.Serializable;

public class LoanAccount extends Account implements Transactable, Notifiable, Serializable {
    private final double principal;
    private double remainingBalance;
    private final double monthlyPayment;

    private static int txCounter = 1;

    /**
     * Creates a loan account with the full principal as the starting debt.
     * The inherited balance field is unused here; remainingBalance tracks what is owed.
     */
    public LoanAccount(String accountId, Customer owner,
                       double principal, double monthlyPayment) {
        super(accountId, owner, 0);
        this.principal = principal;
        this.remainingBalance = principal;
        this.monthlyPayment = monthlyPayment;
    }

    /**
     * Applies a payment toward the loan, reducing remainingBalance.
     * Uses Math.min so the customer can never overpay beyond what is owed.
     * Fires an alert when the loan is completely paid off.
     */
    public void makePayment(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        // Cap the actual payment at what remains so the balance never goes negative
        double actual = Math.min(amount, remainingBalance);
        remainingBalance -= actual;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, actual, "Loan payment"));
        if (remainingBalance == 0) sendAlert("Loan fully paid off!");
    }

    /** Delegates to makePayment — depositing money into a loan account means making a payment. */
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        makePayment(amount);
    }

    /** Withdrawing from a loan account is not a supported operation. */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        throw new UnsupportedOperationException("Cannot withdraw from a loan account.");
    }

    /** Prints an alert message to stdout prefixed with the account ID. */
    @Override
    public void sendAlert(String message) {
        System.out.println("[ALERT][" + accountId + "] " + message);
    }

    @Override
    public AccountType getAccountType() { return AccountType.LOAN; }

    @Override
    public String getSummary() {
        return "LoanAccount[" + accountId + "] Owner: " + owner.getName()
                + " | Principal: $" + principal
                + " | Remaining: $" + String.format("%.2f", remainingBalance)
                + " | Monthly Payment: $" + monthlyPayment;
    }

    public double getPrincipal()        { return principal; }
    public double getRemainingBalance() { return remainingBalance; }
    public double getMonthlyPayment()   { return monthlyPayment; }
}
