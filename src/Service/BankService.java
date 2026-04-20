package Service;

import Model.Account;
import Model.AccountType;
import Model.CheckingAccount;
import Model.CreditAccount;
import Model.Customer;
import Model.LoanAccount;
import Model.SavingsAccount;
import Exceptions.AccountNotFoundException;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/*
 * BankService.java
 * Central registry for all customers and accounts.
 * Handles creation, lookup, deletion, and authentication.
 * TransactionService and InterestService depend on this class to find accounts.
 */
public class BankService implements Serializable {

    private static final String DATA_FILE = "bank_data.ser";
    private final HashMap<String, Customer> customers = new HashMap<>();
    private final HashMap<String, Account>  accounts  = new HashMap<>();
    private final HashMap<String, String>   credentials = new HashMap<>(); // customerId -> password
    private int customerCounter = 1000;
    private int accountCounter  = 1000;

    // ── Customer management ──────────────────────────────────────────

    public Customer createCustomer(String name, String email, String password) {
        String customerId = "CUS-" + customerCounter++;
        Customer customer = new Customer(customerId, name, email);
        customers.put(customerId, customer);
        credentials.put(customerId, password);
        return customer;
    }

    public Customer findCustomer(String customerId) throws AccountNotFoundException {
        Customer customer = customers.get(customerId);
        if (customer == null) throw new AccountNotFoundException(customerId);
        return customer;
    }

    /** Returns true if the password matches the stored credential for this customer. */
    public boolean authenticate(String customerId, String password) {
        return password.equals(credentials.get(customerId));
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    /** Removes the customer and closes all of their accounts. */
    public void deleteCustomer(String customerId) throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        for (Account account : customer.getAccounts()) {
            accounts.remove(account.getAccountId());
        }
        customers.remove(customerId);
        credentials.remove(customerId);
    }

    // ── Account management ───────────────────────────────────────────

    /**
     * Opens a new account of the given type with sensible defaults.
     * For Loan and Credit accounts with custom amounts, use openLoanAccount / openCreditAccount.
     */
    public Account openAccount(String customerId, AccountType accountType)
            throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "ACC-" + accountCounter++;
        Account newAccount = switch (accountType) {
            case SAVINGS  -> new SavingsAccount(accountId, customer, 0.0, 0.03, 0.0);
            case CHECKING -> new CheckingAccount(accountId, customer, 0.0, 200.0);
            case LOAN     -> new LoanAccount(accountId, customer, 0.0, 0.0);
            case CREDIT   -> new CreditAccount(accountId, customer, 1000.0, 0.02,
                                               LocalDate.now().plusMonths(1));
        };
        accounts.put(accountId, newAccount);
        customer.addAccount(newAccount);
        return newAccount;
    }

    /** Opens a loan account with a specific principal and monthly payment amount. */
    public LoanAccount openLoanAccount(String customerId, double principal, double monthlyPayment)
            throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "ACC-" + accountCounter++;
        LoanAccount loan = new LoanAccount(accountId, customer, principal, monthlyPayment);
        accounts.put(accountId, loan);
        customer.addAccount(loan);
        return loan;
    }

    /** Opens a credit account with a specific limit and interest rate. */
    public CreditAccount openCreditAccount(String customerId, double creditLimit, double interestRate)
            throws AccountNotFoundException {
        Customer customer = findCustomer(customerId);
        String accountId = "ACC-" + accountCounter++;
        CreditAccount credit = new CreditAccount(accountId, customer, creditLimit, interestRate,
                LocalDate.now().plusMonths(1));
        accounts.put(accountId, credit);
        customer.addAccount(credit);
        return credit;
    }

    public Account findAccount(String accountId) throws AccountNotFoundException {
        Account account = accounts.get(accountId);
        if (account == null) throw new AccountNotFoundException(accountId);
        return account;
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    /** Removes the account from the registry and from the owner's account list. */
    public void closeAccount(String accountId) throws AccountNotFoundException {
        Account account = findAccount(accountId);
        account.getOwner().getAccounts().remove(account);
        accounts.remove(accountId);
    }

    // ── Persistence ──────────────────────────────────────────────────

    /** Serializes the entire bank state to disk. */
    public void save() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            out.writeObject(this);
        } catch (IOException e) {
            System.out.println("Warning: could not save data — " + e.getMessage());
        }
    }

    /**
     * Loads the bank state from disk if a save file exists.
     * Returns null if no file is found (fresh start).
     */
    public static BankService load() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (BankService) in.readObject();
        } catch (Exception e) {
            System.out.println("Warning: could not load saved data, starting fresh — " + e.getMessage());
            return null;
        }
    }
}
