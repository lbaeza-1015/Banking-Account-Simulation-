package Exceptions;

/*
 * CreditLimitExceededException.java
 * Thrown on CreditAccount when a charge would push the owed amount
 * past the account's credit limit.
 */
public class CreditLimitExceededException extends BankException {
    public CreditLimitExceededException(String accountId, double creditLimit) {
        super("Credit limit exceeded on account " + accountId +
              ". Limit is $" + creditLimit);
    }
}
