package Service;

import Model.Account;
import Model.AccountType;
import Model.Admin;
import Model.CheckingAccount;
import Model.CreditAccount;
import Model.Customer;
import Model.LoanAccount;
import Model.SavingsAccount;
import Exceptions.AccountNotFoundException;
import Exceptions.InvalidAmountException;
import java.time.LocalDate;
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

    public Customer findCustomer(String customerId) throws AccountNotFoundException {
        Customer customer = customers.get(customerId);
        if (customer == null) throw new AccountNotFoundException(customerId);
        return customer;
    }

// more methods will go here


    public Account openAccount(String customerId, AccountType accountType)
            throws AccountNotFoundException {

        Customer customer = findCustomer(customerId);

        String accountId = "ACC-" + accountCounter;
        accountCounter++;
        Account newAccount = switch (accountType) {
            case SAVINGS  -> new SavingsAccount(accountId, customer, 0.0, 0.01, 0.0);
            case CHECKING -> new CheckingAccount(accountId, customer, 0.0, 100.0);
            case LOAN     -> new LoanAccount(accountId, customer, 0.0, 0.0);
            case CREDIT   -> new CreditAccount(accountId, customer, 1000.0, 0.02, LocalDate.now().plusMonths(1));
        };

        accounts.put(accountId, newAccount);
        customer.addAccount(newAccount);

        return newAccount;
    }
}
