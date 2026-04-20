package ui;

import Exceptions.AccountNotFoundException;
import Model.Account;
import Model.Customer;
import Model.Transaction;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.util.StringConverter;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class TransactionHistoryView {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final Stage    stage;
    private final Customer customer;

    public TransactionHistoryView(Stage stage, Customer customer) {
        this.stage    = stage;
        this.customer = customer;
    }

    public void show() {
        // ── Header ────────────────────────────────────────────────────
        Label title = new Label("TRANSACTION HISTORY");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        title.setTextFill(Color.WHITE);
        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(18));
        header.setStyle("-fx-background-color: #1a237e;");

        // ── Account selector ──────────────────────────────────────────
        ComboBox<Account> accountBox = new ComboBox<>();
        accountBox.getItems().addAll(customer.getAccounts());
        accountBox.setConverter(new StringConverter<>() {
            @Override public String toString(Account a) {
                return a == null ? "" : a.getAccountId() + "  —  " + a.getAccountType();
            }
            @Override public Account fromString(String s) { return null; }
        });

        // ── Table ─────────────────────────────────────────────────────
        TableView<Transaction> table = buildTable();

        if (!customer.getAccounts().isEmpty()) {
            accountBox.setValue(customer.getAccounts().get(0));
            loadHistory(table, customer.getAccounts().get(0));
        }

        accountBox.setOnAction(e -> {
            Account selected = accountBox.getValue();
            if (selected != null) loadHistory(table, selected);
        });

        // ── Back button ───────────────────────────────────────────────
        Button backBtn = new Button("← Back to Dashboard");
        backBtn.setStyle("-fx-background-color: #37474f; -fx-text-fill: white; -fx-padding: 8 20;");
        backBtn.setOnAction(e -> new CustomerDashboard(stage, customer).show());

        // ── Layout ────────────────────────────────────────────────────
        HBox selectorRow = new HBox(10, new Label("Account:"), accountBox);
        selectorRow.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(12, selectorRow, table, backBtn);
        content.setPadding(new Insets(20));

        VBox root = new VBox(header, content);
        root.setStyle("-fx-background-color: white;");

        stage.setTitle("Transaction History — " + customer.getName());
        stage.setScene(new Scene(root, 580, 450));
    }

    private TableView<Transaction> buildTable() {
        TableView<Transaction> table = new TableView<>();

        TableColumn<Transaction, String> dateCol = new TableColumn<>("Date & Time");
        dateCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTimestamp().format(FMT)));
        dateCol.setPrefWidth(140);

        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getType().toString()));
        typeCol.setPrefWidth(145);

        TableColumn<Transaction, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(cell ->
                new SimpleStringProperty("$" + String.format("%.2f", cell.getValue().getAmount())));
        amountCol.setPrefWidth(90);

        TableColumn<Transaction, String> noteCol = new TableColumn<>("Note");
        noteCol.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getNote()));
        noteCol.setPrefWidth(160);

        table.getColumns().add(dateCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(amountCol);
        table.getColumns().add(noteCol);
        table.setPrefHeight(290);
        table.setPlaceholder(new Label("No transactions yet."));
        return table;
    }

    private void loadHistory(TableView<Transaction> table, Account account) {
        try {
            List<Transaction> history = AppContext.transactionService.getHistory(account.getAccountId());
            table.setItems(FXCollections.observableArrayList(history));
        } catch (AccountNotFoundException e) {
            table.setItems(FXCollections.emptyObservableList());
        }
    }
}
