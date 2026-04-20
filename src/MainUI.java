import Model.AccountType;
import Model.Customer;
import Service.BankService;
import Service.InterestService;
import Service.TransactionService;
import ui.CustomerDashboard;
import ui.LoginScreen;
import ui.RegisterScreen;

import java.util.Scanner;

public class MainUI {

    public static void main(String[] args) {

        // Load saved state, or start fresh with seed accounts
        BankService bankService = BankService.load();
        if (bankService == null) {
            bankService = new BankService();
            Customer c1 = bankService.createCustomer("Alice", "alice@bank.com", "pass123");
            Customer c2 = bankService.createCustomer("Bob", "bob@bank.com", "qwerty");
            try {
                bankService.openAccount(c1.getCustomerId(), AccountType.SAVINGS);
                bankService.openAccount(c1.getCustomerId(), AccountType.CHECKING);
                bankService.openAccount(c2.getCustomerId(), AccountType.SAVINGS);
            } catch (Exception e) {
                System.out.println("Error opening seed accounts: " + e.getMessage());
            }
        }

        TransactionService transactionService = new TransactionService(bankService);
        InterestService interestService = new InterestService(bankService);

        Scanner scanner = new Scanner(System.in);
        LoginScreen loginScreen = new LoginScreen(bankService);
        RegisterScreen registerScreen = new RegisterScreen(bankService);

        while (true) {
            System.out.println("\n=== WELCOME TO THE BANK ===");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");

            String input = scanner.nextLine().trim();

            if (input.equals("1")) {
                Customer loggedIn = loginScreen.login();
                if (loggedIn != null) {
                    CustomerDashboard dashboard = new CustomerDashboard(loggedIn, transactionService);
                    dashboard.showMenu();
                }

            } else if (input.equals("2")) {
                registerScreen.register();

            } else if (input.equals("3")) {
                bankService.save();
                System.out.println("Data saved. Goodbye.");
                break;

            } else {
                System.out.println("Invalid option.");
            }
        }
    }
}