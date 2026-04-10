package Model;

/*
 * SavingsAccount.java
 * Extends Account. Adds an interestRate and minimumBalance.
 * Withdrawals are blocked if they would drop the balance below the minimum.
 * applyInterest() credits interest directly to the balance.
 * Implements Transactable, InterestBearing, and Notifiable.
 */
import exceptions.InsufficientFundsException;
import exceptions.InvalidAmountException;
import interfaces.InterestBearing;
import interfaces.Notifiable;
import interfaces.Transactable;

public class SavingsAccount extends Account implements Transactable, InterestBearing, Notifiable {
    private double interestRate;
    private final double minimumBalance;

    private static int txCounter = 1;

    public SavingsAccount(String accountId, Customer owner,
                          double initialBalance, double interestRate, double minimumBalance) {
        super(accountId, owner, initialBalance);
        this.interestRate = interestRate;
        this.minimumBalance = minimumBalance;
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
        if (balance - amount < minimumBalance)
            throw new InsufficientFundsException(accountId, amount, balance - minimumBalance);
        balance -= amount;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.WITHDRAWAL, amount, "Withdrawal"));
    }

    @Override
    public double calculateInterest() {
        return balance * interestRate;
    }

    @Override
    public void applyInterest() {
        double interest = calculateInterest();
        balance += interest;
        recordTransaction(new Transaction("TXN-" + txCounter++, accountId,
                TransactionType.INTEREST_APPLIED, interest, "Interest applied"));
    }

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
