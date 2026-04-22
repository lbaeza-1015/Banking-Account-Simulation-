package ui;

import Model.Account;
import Model.Customer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class CustomerDashboard {

    private final Stage    stage;
    private final Customer customer;

    public CustomerDashboard(Stage stage, Customer customer) {
        this.stage    = stage;
        this.customer = customer;
    }

    public void show() {
        // ── Header ────────────────────────────────────────────────────
        Label welcome = new Label("Welcome, " + customer.getName());
        welcome.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        welcome.setTextFill(Color.WHITE);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: #c62828; -fx-text-fill: white; -fx-padding: 5 14;");
        logoutBtn.setOnAction(e -> new LoginScreen(stage).show());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(welcome, spacer, logoutBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: #1a237e;");

        // ── Account list ──────────────────────────────────────────────
        Label accountsTitle = new Label("Your Accounts");
        accountsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        ListView<String> accountList = new ListView<>();
        accountList.setItems(FXCollections.observableArrayList(
                customer.getAccounts().stream()
                        .map(a -> String.format("%-12s  %-10s  $%.2f",
                                a.getAccountId(),
                                a.getAccountType(),
                                a.getBalance()))
                        .toList()
        ));
        accountList.setPrefHeight(120);
        accountList.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 13px;");

        // ── Action buttons ────────────────────────────────────────────
        String primaryStyle = "-fx-background-color: #1a237e; -fx-text-fill: white;"
                            + "-fx-pref-width: 120px; -fx-padding: 9 0;";
        String secondaryStyle = "-fx-background-color: #37474f; -fx-text-fill: white;"
                              + "-fx-pref-width: 180px; -fx-padding: 9 0;";

        Button depositBtn  = new Button("Deposit");
        Button withdrawBtn = new Button("Withdraw");
        Button transferBtn = new Button("Transfer");
        Button historyBtn  = new Button("Transaction History");

        depositBtn.setStyle(primaryStyle);
        withdrawBtn.setStyle(primaryStyle);
        transferBtn.setStyle(primaryStyle);
        historyBtn.setStyle(secondaryStyle);

        TransactionForm form = new TransactionForm(stage, customer, this);
        depositBtn.setOnAction(e  -> form.showDeposit());
        withdrawBtn.setOnAction(e -> form.showWithdraw());
        transferBtn.setOnAction(e -> form.showTransfer());
        historyBtn.setOnAction(e  -> new TransactionHistoryView(stage, customer).show());

        HBox actionRow = new HBox(10, depositBtn, withdrawBtn, transferBtn);
        actionRow.setAlignment(Pos.CENTER);

        HBox historyRow = new HBox(historyBtn);
        historyRow.setAlignment(Pos.CENTER);

        // ── Root ──────────────────────────────────────────────────────
        VBox content = new VBox(14, accountsTitle, accountList, actionRow, historyRow);
        content.setPadding(new Insets(20));

        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: white;");

        stage.setTitle("Dashboard — " + customer.getName());
        stage.setScene(new Scene(root, 460, 360));
    }

    /** Called by TransactionForm after a successful operation to refresh the balance list. */
    public void refresh() {
        show();
    }
}
