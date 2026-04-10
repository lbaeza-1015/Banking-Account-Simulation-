package Interfaces;

/*
 * Transactable.java
 * A contract that says: if you implement me, you must support deposit() and withdraw().
 * Account types like SavingsAccount and CheckingAccount implement this interface.
 */
import Exceptions.InvalidAmountException;
import Exceptions.InsufficientFundsException;

public interface Transactable {
    void deposit(double amount) throws InvalidAmountException;
    void withdraw(double amount) throws InsufficientFundsException, InvalidAmountException;
}
