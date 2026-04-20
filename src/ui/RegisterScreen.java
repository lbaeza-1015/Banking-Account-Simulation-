package ui;

import Exceptions.AccountNotFoundException;
import Model.Account;
import Model.AccountType;
import Model.Customer;
import Service.BankService;

import java.util.Scanner;

public class RegisterScreen {

    private final BankService bankService;

    public RegisterScreen(BankService bankService) {
        this.bankService = bankService;
    }

    public Customer register() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n=== REGISTER NEW ACCOUNT ===");

        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();

        System.out.print("Create a password: ");
        String password = scanner.nextLine();

        Customer customer = bankService.createCustomer(name, email, password);

        System.out.println("\nProfile created! Your Customer ID is: " + customer.getCustomerId());
        System.out.println("(Write this down — you will need it to log in.)");

        openAccountsForCustomer(customer, scanner);

        return customer;
    }

    private void openAccountsForCustomer(Customer customer, Scanner scanner) {
        System.out.println("\nWhich account(s) would you like to open?");
        System.out.println("1. Savings");
        System.out.println("2. Checking");
        System.out.println("3. Both Savings and Checking");

        System.out.print("Choose option: ");
        String input = scanner.nextLine().trim();

        boolean openSavings  = input.equals("1") || input.equals("3");
        boolean openChecking = input.equals("2") || input.equals("3");

        if (!openSavings && !openChecking) {
            System.out.println("Invalid choice — opening a Savings account by default.");
            openSavings = true;
        }

        try {
            if (openSavings) {
                Account savings = bankService.openAccount(customer.getCustomerId(), AccountType.SAVINGS);
                System.out.println("Savings account opened.  Account ID: " + savings.getAccountId());
            }
            if (openChecking) {
                Account checking = bankService.openAccount(customer.getCustomerId(), AccountType.CHECKING);
                System.out.println("Checking account opened. Account ID: " + checking.getAccountId());
            }
        } catch (AccountNotFoundException e) {
            System.out.println("Error opening account: " + e.getMessage());
        }

        System.out.println("\nRegistration complete. You can now log in with your Customer ID and password.");
    }
}
