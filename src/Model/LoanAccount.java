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

    public LoanAccount(String accountId, Customer owner,
                       double principal, double monthlyPayment) {
        super(accountId, owner, 0);
        this.principal = principal;
        this.remainingBalance = principal;
        this.monthlyPayment = monthlyPayment;
    }

    public void makePayment(double amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException(amount);
        double actual = Math.min(amount, remainingBalance);
        remainingBalance -= actual;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, actual, "Loan payment"));
        if (remainingBalance == 0) sendAlert("Loan fully paid off!");
    }

    @Override
    public void deposit(double amount) throws InvalidAmountException {
        makePayment(amount);
    }

    @Override
    public void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException {
        throw new UnsupportedOperationException("Cannot withdraw from a loan account.");
    }

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
