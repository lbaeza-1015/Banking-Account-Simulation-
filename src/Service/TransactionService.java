package Service;

import Exceptions.AccountNotFoundException;
import Exceptions.InsufficientFundsException;
import Exceptions.InvalidAmountException;
import Exceptions.OverdraftException;
import Interfaces.Notifiable;
import Interfaces.Transactable;
import Model.Account;
import Model.Transaction;

import java.util.List;

/*
 * TransactionService.java
 * Executes all money movements: deposit, withdraw, transfer.
 * This is the class Person C's UI calls for every transaction operation.
 * Fires Notifiable alerts when balance drops below LOW_BALANCE_THRESHOLD after a withdrawal.
 */
public class TransactionService {
    private static final double LOW_BALANCE_THRESHOLD = 100.0;
    private final BankService bank;

    /** Creates the service using the shared BankService as the account registry. */
    public TransactionService(BankService bank) {
        this.bank = bank;
    }

    /** Deposits amount into the given account. */
    public void deposit(String accountId, double amount)
            throws AccountNotFoundException, InvalidAmountException {
        Account acc = bank.findAccount(accountId);
        ((Transactable) acc).deposit(amount);
    }

    /**
     * Withdraws amount from the given account.
     * Fires a low-balance alert via Notifiable if balance drops below $100.
     */
    public void withdraw(String accountId, double amount)
            throws AccountNotFoundException, InvalidAmountException,
                   InsufficientFundsException, OverdraftException {
        Account acc = bank.findAccount(accountId);
        ((Transactable) acc).withdraw(amount);
        if (acc.getBalance() < LOW_BALANCE_THRESHOLD && acc instanceof Notifiable n) {
            n.sendAlert("Warning: balance below $" + LOW_BALANCE_THRESHOLD
                    + " on account " + accountId);
        }
    }

    /**
     * Transfers amount from one account to another.
     * Both accounts are resolved before any money moves — no partial transfers.
     */
    public void transfer(String fromId, String toId, double amount)
            throws AccountNotFoundException, InsufficientFundsException, InvalidAmountException {
        Account from = bank.findAccount(fromId);
        Account to   = bank.findAccount(toId);
        ((Transactable) from).withdraw(amount);
        ((Transactable) to).deposit(amount);
    }

    /** Returns the full transaction history for an account. */
    public List<Transaction> getHistory(String accountId)
            throws AccountNotFoundException {
        return bank.findAccount(accountId).getHistory();
    }
}
