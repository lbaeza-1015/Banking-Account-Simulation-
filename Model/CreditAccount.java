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

import java.time.LocalDate;

public class CreditAccount extends Account implements Transactable, InterestBearing, Notifiable {
    private final double creditLimit;
    private double amountOwed;
    private final double interestRate;
    private final LocalDate dueDate;

    private static int txCounter = 1;

    public CreditAccount(String accountId, Customer owner,
                         double creditLimit, double interestRate, LocalDate dueDate) {
        super(accountId, owner, 0);
        this.creditLimit = creditLimit;
        this.interestRate = interestRate;
        this.dueDate = dueDate;
        this.amountOwed = 0;
    }

    public void charge(double amount) throws InvalidAmountException, CreditLimitExceededException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        if (amountOwed + amount > creditLimit)
            throw new CreditLimitExceededException(accountId, creditLimit);
        amountOwed += amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, amount, "Credit charge"));
    }

    public void payBalance(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        double actual = Math.min(amount, amountOwed);
        amountOwed -= actual;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.DEPOSIT, actual, "Credit payment"));
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        payBalance(amount);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        charge(amount);
    }

    @Override
    public double calculateInterest() {
        return amountOwed * interestRate;
    }

    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        amountOwed += interest;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.INTEREST_APPLIED, interest, "Interest applied"));
    }

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
