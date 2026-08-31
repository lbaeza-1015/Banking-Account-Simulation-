import Model.AccountType;
import Model.Customer;
import Service.BankService;
import Service.InterestService;
import Service.TransactionService;
import javafx.application.Application;
import javafx.stage.Stage;
import ui.AppContext;
import ui.LoginScreen;

/*
 * MainApp.java
 * JavaFX entry point. Replaces MainUI for the graphical version.
 * Loads saved bank state (or seeds Alice/Bob on first run),
 * opens the login window, and saves on close.
 */
public class MainApp extends Application {

    /**
     * JavaFX entry point. Loads persisted bank state from disk, or seeds two demo customers
     * (Alice and Bob) when no save file exists. Wires up shared services in AppContext and
     * opens the login window.
     */
    @Override
    public void start(Stage primaryStage) {
        BankService bank = BankService.load();
        if (bank == null) {
            // First run — create demo customers so the app is immediately usable
            bank = new BankService();
            Customer c1 = bank.createCustomer("Alice", "alice@bank.com", "pass123");
            Customer c2 = bank.createCustomer("Bob",   "bob@bank.com",   "qwerty");
            try {
                bank.openAccount(c1.getCustomerId(), AccountType.SAVINGS);
                bank.openAccount(c1.getCustomerId(), AccountType.CHECKING);
                bank.openAccount(c2.getCustomerId(), AccountType.SAVINGS);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AppContext.bankService        = bank;
        AppContext.transactionService = new TransactionService(bank);
        AppContext.interestService    = new InterestService(bank);

        primaryStage.setResizable(false);
        new LoginScreen(primaryStage).show();
        primaryStage.show();
    }

    /** Called by JavaFX when the window closes; persists the current bank state to disk. */
    @Override
    public void stop() {
        if (AppContext.bankService != null) AppContext.bankService.save();
    }

    /** Launches the JavaFX application. */
    public static void main(String[] args) {
        launch(args);
    }
}
