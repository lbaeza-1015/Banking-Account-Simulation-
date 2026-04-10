package service;

import model.Account;
import model.Customer;
import model.Admin;
import exceptions.AccountNotFoundException;
import exceptions.InvalidAmountException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
public class BankService {
    private HashMap<String, Customer> customers;
    private HashMap<String, Account> accounts;
    private int customerCounter;
    private int accountCounter;


    public BankService() {
        this.customers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.customerCounter = 1000;
        this.accountCounter = 1000;
    }
    public Customer createCustomer(String name, String password) {
        String customerId = "CUS-" + customerCounter;
        customerCounter++;

        Customer newCustomer = new Customer(customerId, name, password);
        customers.put(customerId, newCustomer);

        return newCustomer;
    }

// more methods will go here


public Account openAccount(String customerId, AccountType accountType)
        throws AccountNotFoundException {

    Customer customer = findCustomer(customerId);

    String accountId = "ACC-" + accountCounter;
    accountCounter++;
    Account newAccount = switch (accountType) {
        case SAVINGS  -> new SavingsAccount(accountId, customer);
        case CHECKING -> new CheckingAccount(accountId, customer);
        case LOAN     -> new LoanAccount(accountId, customer);
        case CREDIT   -> new CreditAccount(accountId, customer);
    };

    accounts.put(accountId, newAccount);
    customer.addAccount(newAccount);

    return newAccount;
}
