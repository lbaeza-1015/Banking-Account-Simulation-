package Interfaces;

/*
 * InterestBearing.java
 * A contract for accounts that earn or charge interest.
 * Requires calculateInterest() and applyInterest().
 * Implemented by SavingsAccount and CreditAccount.
 */
public interface InterestBearing {
    double calculateInterest();
    void applyInterest();
    void removeInterest();
}
