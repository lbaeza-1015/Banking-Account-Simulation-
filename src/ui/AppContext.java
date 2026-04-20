package ui;

import Service.BankService;
import Service.InterestService;
import Service.TransactionService;

/*
 * AppContext.java
 * Holds static references to the shared service instances so every
 * ui screen can reach them without threading them through every constructor.
 * Populated once by MainApp at startup.
 */
public class AppContext {
    public static BankService        bankService;
    public static TransactionService transactionService;
    public static InterestService    interestService;
}
