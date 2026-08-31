package ui;

import Model.Account;
import Model.Customer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/*
 * AdminDashboard.java
 * The administrator's control panel. Provides three actions:
 *   - View all customers and their accounts
 *   - View the detailed summary of every account in the system
 *   - Apply monthly interest to all InterestBearing accounts
 * Results are printed to a read-only text area on the right side of the screen.
 */
public class AdminDashboard {

    private final Stage stage;

    /** Creates an admin dashboard rendered on the given stage. */
    public AdminDashboard(Stage stage) {
        this.stage = stage;
    }

    /**
     * Builds and displays the admin dashboard. Button handlers write their output
     * directly into the shared TextArea so the admin can review results without
     * navigating away.
     */
    public void show() {
        // ── Header ────────────────────────────────────────────────────
        Label title = new Label("ADMIN DASHBOARD");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(18));
        header.setStyle("-fx-background-color: #37474f;");

        // ── Output area ───────────────────────────────────────────────
        TextArea output = new TextArea();
        output.setEditable(false);
        output.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        output.setPrefHeight(300);

        // ── Buttons ───────────────────────────────────────────────────
        String btnStyle = "-fx-background-color: #37474f; -fx-text-fill: white;"
                        + "-fx-pref-width: 210px; -fx-padding: 9 0;";

        Button customersBtn      = new Button("View All Customers");
        Button accountsBtn       = new Button("View All Accounts");
        Button interestBtn       = new Button("Apply Monthly Interest");
        Button removeInterestBtn = new Button("Remove Monthly Interest");
        Button logoutBtn         = new Button("Logout");

        customersBtn.setStyle(btnStyle);
        accountsBtn.setStyle(btnStyle);
        interestBtn.setStyle(btnStyle);
        removeInterestBtn.setStyle(btnStyle);
        logoutBtn.setStyle("-fx-background-color: #c62828; -fx-text-fill: white;"
                         + "-fx-pref-width: 210px; -fx-padding: 9 0;");

        // Lists every customer followed by their accounts (brief toString format)
        customersBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Customer c : AppContext.bankService.getAllCustomers()) {
                sb.append(c).append("\n");
                for (Account a : c.getAccounts())
                    sb.append("  ").append(a).append("\n");
                sb.append("\n");
            }
            output.setText(sb.toString());
        });

        // Lists every account across all customers using the detailed getSummary() format
        accountsBtn.setOnAction(e -> {
            StringBuilder sb = new StringBuilder();
            for (Account a : AppContext.bankService.getAllAccounts())
                sb.append(a.getSummary()).append("\n\n");
            output.setText(sb.toString());
        });

        // Applies interest to all SavingsAccount and CreditAccount instances
        interestBtn.setOnAction(e -> {
            AppContext.interestService.applyMonthlyInterest();
            output.setText("Monthly interest applied to all eligible accounts.");
        });

        // Reverses the last interest application on all SavingsAccount and CreditAccount instances
        removeInterestBtn.setOnAction(e -> {
            AppContext.interestService.removeMonthlyInterest();
            output.setText("Monthly interest removed from all eligible accounts.");
        });

        logoutBtn.setOnAction(e -> new LoginScreen(stage).show());

        VBox buttons = new VBox(10, customersBtn, accountsBtn, interestBtn, removeInterestBtn, logoutBtn);
        buttons.setAlignment(Pos.TOP_CENTER);

        HBox content = new HBox(15, buttons, output);
        HBox.setHgrow(output, Priority.ALWAYS);
        content.setPadding(new Insets(20));

        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: white;");

        stage.setTitle("Admin Dashboard");
        stage.setScene(new Scene(root, 620, 430));
    }
}
