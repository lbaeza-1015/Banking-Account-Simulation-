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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class TransactionForm {

    private final Stage             owner;
    private final Customer          customer;
    private final CustomerDashboard dashboard;

    public TransactionForm(Stage owner, Customer customer, CustomerDashboard dashboard) {
        this.owner     = owner;
        this.customer  = customer;
        this.dashboard = dashboard;
    }

    // ── Deposit ───────────────────────────────────────────────────────

    public void showDeposit() {
        Stage dialog = makeDialog("Deposit");

        ComboBox<Account> accountBox = buildAccountCombo();
        TextField amountField = amountInput();
        Label errorLabel = errorLabel();

        Button confirmBtn = confirmButton("Deposit");
        confirmBtn.setOnAction(e -> {
            Account selected = accountBox.getValue();
            if (selected == null) { errorLabel.setText("Select an account."); return; }
            Double amount = parseAmount(amountField, errorLabel);
            if (amount == null) return;
            try {
                AppContext.transactionService.deposit(selected.getAccountId(), amount);
                dialog.close();
                dashboard.refresh();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        dialog.setScene(new Scene(
                buildTwoFieldForm("Deposit Funds", "Account:", accountBox, "Amount:", amountField, errorLabel, confirmBtn),
                340, 295
        ));
        dialog.showAndWait();
    }

    // ── Withdraw ──────────────────────────────────────────────────────

    public void showWithdraw() {
        Stage dialog = makeDialog("Withdraw");

        ComboBox<Account> accountBox = buildAccountCombo();
        TextField amountField = amountInput();
        Label errorLabel = errorLabel();

        Button confirmBtn = confirmButton("Withdraw");
        confirmBtn.setOnAction(e -> {
            Account selected = accountBox.getValue();
            if (selected == null) { errorLabel.setText("Select an account."); return; }
            Double amount = parseAmount(amountField, errorLabel);
            if (amount == null) return;
            try {
                AppContext.transactionService.withdraw(selected.getAccountId(), amount);
                dialog.close();
                dashboard.refresh();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        dialog.setScene(new Scene(
                buildTwoFieldForm("Withdraw Funds", "Account:", accountBox, "Amount:", amountField, errorLabel, confirmBtn),
                340, 295
        ));
        dialog.showAndWait();
    }

    // ── Transfer ──────────────────────────────────────────────────────

    public void showTransfer() {
        Stage dialog = makeDialog("Transfer");

        ComboBox<Account> fromBox = buildAccountCombo();
        TextField toField = new TextField();
        toField.setPromptText("Destination Account ID (e.g. ACC-1002)");
        TextField amountField = amountInput();
        Label errorLabel = errorLabel();

        Button confirmBtn = confirmButton("Transfer");
        confirmBtn.setOnAction(e -> {
            Account selected = fromBox.getValue();
            if (selected == null) { errorLabel.setText("Select a source account."); return; }
            String toId = toField.getText().trim();
            if (toId.isEmpty()) { errorLabel.setText("Enter a destination account ID."); return; }
            Double amount = parseAmount(amountField, errorLabel);
            if (amount == null) return;
            try {
                AppContext.transactionService.transfer(selected.getAccountId(), toId, amount);
                dialog.close();
                dashboard.refresh();
            } catch (Exception ex) {
                errorLabel.setText(ex.getMessage());
            }
        });

        VBox form = new VBox(10,
                new Label("From Account:"), fromBox,
                new Label("To Account ID:"), toField,
                new Label("Amount:"), amountField,
                errorLabel, confirmBtn
        );
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(makeHeader("Transfer Funds"), form);
        root.setStyle("-fx-background-color: white;");

        dialog.setScene(new Scene(root, 360, 350));
        dialog.showAndWait();
    }

    // ── Helpers ───────────────────────────────────────────────────────

    private Stage makeDialog(String title) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle(title);
        dialog.setResizable(false);
        return dialog;
    }

    private ComboBox<Account> buildAccountCombo() {
        List<Account> accounts = customer.getAccounts();
        ComboBox<Account> box = new ComboBox<>();
        box.getItems().addAll(accounts);
        box.setConverter(new StringConverter<>() {
            @Override public String toString(Account a) {
                return a == null ? "" : a.getAccountId() + "  —  "
                        + a.getAccountType() + "  ($" + String.format("%.2f", a.getBalance()) + ")";
            }
            @Override public Account fromString(String s) { return null; }
        });
        box.setMaxWidth(Double.MAX_VALUE);
        if (!accounts.isEmpty()) box.setValue(accounts.get(0));
        return box;
    }

    private TextField amountInput() {
        TextField f = new TextField();
        f.setPromptText("0.00");
        return f;
    }

    private Label errorLabel() {
        Label l = new Label();
        l.setTextFill(Color.RED);
        l.setWrapText(true);
        return l;
    }

    private Button confirmButton(String text) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"
                 + "-fx-font-size: 13px; -fx-padding: 9 0;");
        b.setDefaultButton(true);
        return b;
    }

    private Double parseAmount(TextField field, Label errorLabel) {
        try {
            double v = Double.parseDouble(field.getText().trim());
            return v;
        } catch (NumberFormatException e) {
            errorLabel.setText("Enter a valid amount.");
            return null;
        }
    }

    private HBox makeHeader(String text) {
        Label lbl = new Label(text);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        lbl.setTextFill(Color.WHITE);
        HBox h = new HBox(lbl);
        h.setAlignment(Pos.CENTER);
        h.setPadding(new Insets(15));
        h.setStyle("-fx-background-color: #1a237e;");
        return h;
    }

    private VBox buildTwoFieldForm(String headerText,
                                   String label1, Control field1,
                                   String label2, Control field2,
                                   Label errorLabel, Button confirmBtn) {
        VBox form = new VBox(10,
                new Label(label1), field1,
                new Label(label2), field2,
                errorLabel, confirmBtn
        );
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER_LEFT);

        VBox root = new VBox(makeHeader(headerText), form);
        root.setStyle("-fx-background-color: white;");
        return root;
    }
}
