import Exceptions.AccountNotFoundException;
import Exceptions.CreditLimitExceededException;
import Exceptions.InsufficientFundsException;
import Exceptions.InvalidAmountException;
import Model.Account;
import Model.AccountType;
import Model.CreditAccount;
import Model.Customer;
import Model.LoanAccount;
import Model.Transaction;
import Service.BankService;
import Service.InterestService;
import Service.TransactionService;

import java.util.List;

/*
 * Main.java
 * Console demo — runs without JavaFX.
 * Covers every account type, every exception, transfers, interest, and alerts.
 */
public class Main {

    static final String LINE = "─".repeat(55);

    public static void main(String[] args) {

        BankService bank = new BankService();
        TransactionService tx = new TransactionService(bank);
        InterestService interest = new InterestService(bank);

        // ── 1. Create customers ──────────────────────────────────────
        section("1. Creating customers");
        Customer alice = bank.createCustomer("Alice", "alice@bank.com", "pass123");
        Customer bob   = bank.createCustomer("Bob",   "bob@bank.com",   "qwerty");
        System.out.println("Created: " + alice);
        System.out.println("Created: " + bob);

        // ── 2. Authentication ────────────────────────────────────────
        section("2. Authentication");
        System.out.println("Alice correct password : " + bank.authenticate(alice.getCustomerId(), "pass123"));
        System.out.println("Alice wrong password   : " + bank.authenticate(alice.getCustomerId(), "wrong"));

        // ── 3. Open accounts ─────────────────────────────────────────
        section("3. Opening accounts");
        Account aliceSavings  = bank.openAccount(alice.getCustomerId(), AccountType.SAVINGS);
        Account aliceChecking = bank.openAccount(alice.getCustomerId(), AccountType.CHECKING);
        LoanAccount aliceLoan = bank.openLoanAccount(alice.getCustomerId(), 5000.0, 250.0);
        CreditAccount aliceCredit = bank.openCreditAccount(alice.getCustomerId(), 2000.0, 0.02);
        Account bobSavings = bank.openAccount(bob.getCustomerId(), AccountType.SAVINGS);
        System.out.println(aliceSavings.getSummary());
        System.out.println(aliceChecking.getSummary());
        System.out.println(aliceLoan.getSummary());
        System.out.println(aliceCredit.getSummary());
        System.out.println(bobSavings.getSummary());

        // ── 4. Deposits ──────────────────────────────────────────────
        section("4. Deposits");
        tryDeposit(tx, aliceSavings.getAccountId(),  1500.0);
        tryDeposit(tx, aliceChecking.getAccountId(),  800.0);
        tryDeposit(tx, bobSavings.getAccountId(),     300.0);
        tryDeposit(tx, aliceSavings.getAccountId(),    -50.0); // InvalidAmountException expected

        // ── 5. Withdrawals ───────────────────────────────────────────
        section("5. Withdrawals");
        tryWithdraw(tx, aliceSavings.getAccountId(),  200.0);
        tryWithdraw(tx, aliceChecking.getAccountId(), 750.0); // drops to $50 → low-balance alert
        tryWithdraw(tx, aliceSavings.getAccountId(), 9999.0); // InsufficientFundsException expected
        tryWithdraw(tx, aliceChecking.getAccountId(), 500.0); // OverdraftException expected ($50 - $500 < -$200 limit)

        // ── 6. Transfers ─────────────────────────────────────────────
        section("6. Transfers");
        tryTransfer(tx, aliceSavings.getAccountId(), bobSavings.getAccountId(), 300.0);
        System.out.println("Alice savings after transfer : " + aliceSavings);
        System.out.println("Bob savings after transfer   : " + bobSavings);

        // ── 7. Loan payments ─────────────────────────────────────────
        section("7. Loan payments");
        System.out.println("Before: " + aliceLoan.getSummary());
        tryDeposit(tx, aliceLoan.getAccountId(), 250.0);
        tryDeposit(tx, aliceLoan.getAccountId(), 250.0);
        System.out.println("After 2 payments: " + aliceLoan.getSummary());

        // ── 8. Credit account ────────────────────────────────────────
        section("8. Credit account charges & payment");
        System.out.println("Before: " + aliceCredit.getSummary());
        tryWithdraw(tx, aliceCredit.getAccountId(), 500.0);   // charge $500
        tryWithdraw(tx, aliceCredit.getAccountId(), 800.0);   // charge $800 → now $1300 owed
        tryWithdraw(tx, aliceCredit.getAccountId(), 800.0);   // CreditLimitExceededException expected
        tryDeposit(tx, aliceCredit.getAccountId(), 1300.0);   // pay full balance
        System.out.println("After pay-off: " + aliceCredit.getSummary());

        // ── 9. Monthly interest ──────────────────────────────────────
        section("9. Monthly interest (Savings + Credit)");
        System.out.println("Alice savings before interest : " + aliceSavings);
        interest.applyMonthlyInterest();
        System.out.println("Alice savings after interest  : " + aliceSavings);

        // ── 10. Transaction history ──────────────────────────────────
        section("10. Transaction history — Alice Savings");
        printHistory(tx, aliceSavings.getAccountId());

        // ── 11. Admin view — all accounts ────────────────────────────
        section("11. Admin view — all accounts");
        bank.getAllCustomers().forEach(c -> {
            System.out.println("\n" + c);
            c.getAccounts().forEach(a -> System.out.println("  " + a));
        });

        // ── 12. Account not found ────────────────────────────────────
        section("12. AccountNotFoundException");
        try {
            tx.deposit("ACC-9999", 100.0);
        } catch (AccountNotFoundException e) {
            System.out.println("Caught: " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("Caught: " + e.getMessage());
        }

        section("Demo complete");
    }

    // ── Helpers ──────────────────────────────────────────────────────

    static void section(String title) {
        System.out.println("\n" + LINE);
        System.out.println("  " + title);
        System.out.println(LINE);
    }

    static void tryDeposit(TransactionService tx, String accountId, double amount) {
        try {
            tx.deposit(accountId, amount);
            System.out.printf("  Deposited $%.2f into %s%n", amount, accountId);
        } catch (InvalidAmountException e) {
            System.out.println("  [InvalidAmount] " + e.getMessage());
        } catch (AccountNotFoundException e) {
            System.out.println("  [NotFound] " + e.getMessage());
        }
    }

    static void tryWithdraw(TransactionService tx, String accountId, double amount) {
        try {
            tx.withdraw(accountId, amount);
            System.out.printf("  Withdrew $%.2f from %s%n", amount, accountId);
        } catch (InsufficientFundsException e) {
            System.out.println("  [InsufficientFunds] " + e.getMessage());
        } catch (CreditLimitExceededException e) {
            System.out.println("  [CreditLimitExceeded] " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("  [InvalidAmount] " + e.getMessage());
        } catch (AccountNotFoundException e) {
            System.out.println("  [NotFound] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  [" + e.getClass().getSimpleName() + "] " + e.getMessage());
        }
    }

    static void tryTransfer(TransactionService tx, String fromId, String toId, double amount) {
        try {
            tx.transfer(fromId, toId, amount);
            System.out.printf("  Transferred $%.2f from %s to %s%n", amount, fromId, toId);
        } catch (InsufficientFundsException e) {
            System.out.println("  [InsufficientFunds] " + e.getMessage());
        } catch (InvalidAmountException e) {
            System.out.println("  [InvalidAmount] " + e.getMessage());
        } catch (AccountNotFoundException e) {
            System.out.println("  [NotFound] " + e.getMessage());
        }
    }

    static void printHistory(TransactionService tx, String accountId) {
        try {
            List<Transaction> history = tx.getHistory(accountId);
            if (history.isEmpty()) {
                System.out.println("  No transactions.");
            } else {
                history.forEach(t -> System.out.println("  " + t));
            }
        } catch (AccountNotFoundException e) {
            System.out.println("  [NotFound] " + e.getMessage());
        }
    }
}
