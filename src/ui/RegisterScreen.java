package ui;

import Model.Account;
import Model.AccountType;
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
 * RegisterScreen.java
 * Displays the new-customer registration form. Collects name, email, password,
 * and the account type(s) to open. On success, shows a confirmation dialog with
 * the generated Customer ID and account IDs, then redirects to LoginScreen.
 */
public class RegisterScreen {

    private final Stage stage;

    /** Creates a RegisterScreen that will render on the given JavaFX stage. */
    public RegisterScreen(Stage stage) {
        this.stage = stage;
    }

    /** Builds and displays the registration form on the stage. */
    public void show() {
        // ── Header ────────────────────────────────────────────────────
        Label title = new Label("CREATE AN ACCOUNT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        title.setTextFill(Color.WHITE);

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(22));
        header.setStyle("-fx-background-color: #1a237e;");

        // ── Form fields ───────────────────────────────────────────────
        Label nameLabel = new Label("Full Name:");
        TextField nameField = new TextField();

        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Label pwLabel = new Label("Password:");
        PasswordField pwField = new PasswordField();

        Label accountLabel = new Label("Open account(s):");
        CheckBox savingsBox  = new CheckBox("Savings");
        CheckBox checkingBox = new CheckBox("Checking");
        savingsBox.setSelected(true);
        HBox checkRow = new HBox(20, savingsBox, checkingBox);

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);

        // ── Buttons ───────────────────────────────────────────────────
        Button registerBtn = new Button("Create Account");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"
                           + "-fx-font-size: 13px; -fx-padding: 10 0;");
        registerBtn.setDefaultButton(true);

        Button backBtn = new Button("← Back to Login");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1a237e;"
                       + "-fx-underline: true; -fx-cursor: hand; -fx-padding: 0;");

        // ── Actions ───────────────────────────────────────────────────
        registerBtn.setOnAction(e -> handleRegister(
                nameField, emailField, pwField,
                savingsBox, checkingBox, errorLabel
        ));
        backBtn.setOnAction(e -> new LoginScreen(stage).show());

        // ── Layout ────────────────────────────────────────────────────
        VBox form = new VBox(10,
                nameLabel,    nameField,
                emailLabel,   emailField,
                pwLabel,      pwField,
                accountLabel, checkRow,
                errorLabel,
                registerBtn,
                backBtn
        );
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(25, 40, 25, 40));

        VBox root = new VBox(header, form);
        root.setStyle("-fx-background-color: white;");

        stage.setTitle("Register");
        stage.setScene(new Scene(root, 400, 460));
    }

    /**
     * Validates the form, creates the customer, opens the selected accounts, then shows
     * a success dialog with the generated IDs. At least one account type must be checked.
     */
    private void handleRegister(TextField nameField, TextField emailField,
                                PasswordField pwField,
                                CheckBox savingsBox, CheckBox checkingBox,
                                Label errorLabel) {
        String name  = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pw    = pwField.getText();

        if (name.isEmpty() || email.isEmpty() || pw.isEmpty()) {
            errorLabel.setText("All fields are required.");
            return;
        }
        if (!savingsBox.isSelected() && !checkingBox.isSelected()) {
            errorLabel.setText("Select at least one account type.");
            return;
        }

        Customer customer = AppContext.bankService.createCustomer(name, email, pw);
        StringBuilder info = new StringBuilder();
        info.append("Customer ID:  ").append(customer.getCustomerId()).append("\n\nAccounts opened:\n");

        try {
            if (savingsBox.isSelected()) {
                Account a = AppContext.bankService.openAccount(customer.getCustomerId(), AccountType.SAVINGS);
                info.append("  Savings:  ").append(a.getAccountId()).append("\n");
            }
            if (checkingBox.isSelected()) {
                Account a = AppContext.bankService.openAccount(customer.getCustomerId(), AccountType.CHECKING);
                info.append("  Checking: ").append(a.getAccountId()).append("\n");
            }
        } catch (Exception ex) {
            errorLabel.setText("Error opening accounts: " + ex.getMessage());
            return;
        }

        info.append("\nWrite these down — you need them to log in.");
        showSuccess(info.toString());
        new LoginScreen(stage).show();
    }

    /** Displays a modal information dialog with the newly created customer and account IDs. */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Successful");
        alert.setHeaderText("Your account has been created!");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
