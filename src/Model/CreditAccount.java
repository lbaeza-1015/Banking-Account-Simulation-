package Model;

/*
 * CreditAccount.java
 * Extends Account. Works like a credit card: charge() adds to what is owed,
 * payBalance() reduces it. Has a credit limit, interest rate, and due date.
 * applyInterest() adds interest charges to the amount owed.
 * Implements Transactable, InterestBearing, and Notifiable.
 */
import Exceptions.CreditLimitExceededException;
import Exceptions.InvalidAmountException;
import Exceptions.InsufficientFundsException;
import Interfaces.InterestBearing;
import Interfaces.Notifiable;
import Interfaces.Transactable;
import java.io.Serializable;
import java.time.LocalDate;

public class CreditAccount extends Account implements Transactable, InterestBearing, Notifiable, Serializable {
    private final double creditLimit;
    private double amountOwed;
    private final double interestRate;
    private final LocalDate dueDate;

    private static int txCounter = 1;

    /** Creates a credit account with a spending limit, interest rate, and initial payment due date. */
    public CreditAccount(String accountId, Customer owner,
                         double creditLimit, double interestRate, LocalDate dueDate) {
        super(accountId, owner, 0);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.dueDate = dueDate;
        this.amountOwed = 0;
    }

    /**
     * Adds a purchase charge to the amount owed.
     * Throws CreditLimitExceededException if the new total would surpass the credit limit.
     */
    public void charge(double amount) throws InvalidAmountException, CreditLimitExceededException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (amountOwed + amount > creditLimit)
            throw new CreditLimitExceededException(accountId, creditLimit);
        amountOwed += amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, amount, "Credit charge"));
    }

    /**
     * Reduces the amount owed by the given payment.
     * Uses Math.min so the customer can never overpay beyond what is owed.
     */
    public void payBalance(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        // Cap the payment at what is currently owed to avoid a negative balance
        double actual = Math.min(amount, amountOwed);
        amountOwed -= actual;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.DEPOSIT, actual, "Credit payment"));
    }

    /** Delegates to payBalance — a deposit on a credit account means paying the bill. */
    @Override
    public void deposit(double amount) throws InvalidAmountException {
        payBalance(amount);
    }

    /** Delegates to charge — a withdrawal on a credit account means making a purchase. */
    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        charge(amount);
    }

    /** Returns the interest charge for this period (amountOwed × rate). */
    @Override
    public double calculateInterest() {
        return amountOwed * interestRate;
    }

    /** Adds the calculated interest to the amount owed and records an INTEREST_APPLIED transaction. */
    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        amountOwed += interest;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.INTEREST_APPLIED, interest, "Interest applied"));
    }

    /** Reverses the last interest application by removing amountOwed * rate / (1 + rate) from the debt. */
    @Override
    public void removeInterest() {
        double interest = amountOwed * interestRate / (1 + interestRate);
        amountOwed -= interest;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.INTEREST_REMOVED, interest, "Interest removed"));
    }

    /** Prints an alert message to stdout prefixed with the account ID. */
    @Override
    public void sendAlert(String message) {
        System.out.println("[ALERT][" + accountId + "] " + message);
    }

    @Override
    public AccountType getAccountType() { return AccountType.CREDIT; }

    @Override
    public String getSummary() {
        return "CreditAccount[" + accountId + "] Owner: " + owner.getName()
                + " | Limit: $" + creditLimit
                + " | Owed: $" + String.format("%.2f", amountOwed)
                + " | Rate: " + (interestRate * 100) + "%"
                + " | Due: " + dueDate;
    }

    public double getCreditLimit()  { return creditLimit; }
    public double getAmountOwed()   { return amountOwed; }
    public double getInterestRate() { return interestRate; }
    public LocalDate getDueDate()   { return dueDate; }
}
