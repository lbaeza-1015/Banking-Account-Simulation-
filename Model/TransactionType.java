package Model;

/*
 * TransactionType.java
 * Labels the 4 kinds of money movements: DEPOSIT, WITHDRAWAL, TRANSFER, INTEREST_APPLIED.
 * Every Transaction object uses this to describe what happened.
 */
public enum TransactionType {
    DEPOSIT,
    WITHDRAWAL,
    TRANSFER,
    INTEREST_APPLIED
}
