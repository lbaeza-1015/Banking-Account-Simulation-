package ui;

import Exceptions.AccountNotFoundException;
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
 * LoginScreen.java
 * Displays the login form where a customer enters their Customer ID and password.
 * On success, navigates to CustomerDashboard. On failure, shows an inline error message.
 * A "Register here" link navigates to RegisterScreen.
 */
public class LoginScreen {

    private final Stage stage;

    /** Creates a LoginScreen that will render on the given JavaFX stage. */
    public LoginScreen(Stage stage) {
        this.stage = stage;
    }

    /** Builds and displays the login form, replacing whatever scene is currently on the stage. */
    public void show() {
        // ── Header ────────────────────────────────────────────────────
        Label title = new Label("WELCOME TO THE BANK");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.WHITE);

        HBox header = new HBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(25));
        header.setStyle("-fx-background-color: #1a237e;");

        // ── Form fields ───────────────────────────────────────────────
        Label idLabel = new Label("Customer ID:");
        TextField idField = new TextField();
        idField.setPromptText("e.g. CUS-1000");

        Label pwLabel = new Label("Password:");
        PasswordField pwField = new PasswordField();

        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);
        errorLabel.setWrapText(true);

        // ── Buttons ───────────────────────────────────────────────────
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-background-color: #1a237e; -fx-text-fill: white;"
                        + "-fx-font-size: 13px; -fx-padding: 10 0;");
        loginBtn.setDefaultButton(true);

        Separator sep = new Separator();

        Label registerPrompt = new Label("Don't have an account?");
        registerPrompt.setTextFill(Color.GRAY);

        Button registerBtn = new Button("Register here");
        registerBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #1a237e;"
                           + "-fx-underline: true; -fx-cursor: hand; -fx-padding: 0;");

        // ── Actions ───────────────────────────────────────────────────
        loginBtn.setOnAction(e -> handleLogin(idField, pwField, errorLabel));
        registerBtn.setOnAction(e -> new RegisterScreen(stage).show());

        // Allow pressing Enter from the ID field to move to password
        idField.setOnAction(e -> pwField.requestFocus());

        // ── Layout ────────────────────────────────────────────────────
        VBox form = new VBox(10,
                idLabel,   idField,
                pwLabel,   pwField,
                errorLabel,
                loginBtn,
                sep,
                registerPrompt, registerBtn
        );
        form.setAlignment(Pos.CENTER_LEFT);
        form.setPadding(new Insets(30, 40, 30, 40));

        VBox root = new VBox(header, form);
        root.setStyle("-fx-background-color: white;");

        stage.setTitle("Bank Login");
        stage.setScene(new Scene(root, 380, 390));
    }

    /**
     * Validates the entered credentials and navigates to the customer dashboard.
     * Shows an inline error message if either field is blank, the password is wrong,
     * or the customer ID does not exist.
     */
    private void handleLogin(TextField idField, PasswordField pwField, Label errorLabel) {
        String id = idField.getText().trim();
        String pw = pwField.getText();

        if (id.isEmpty() || pw.isEmpty()) {
            errorLabel.setText("Please fill in both fields.");
            return;
        }
        if (id.equals("admin") && pw.equals("admin123")) {
            new AdminDashboard(stage).show();
            return;
        }
        if (!AppContext.bankService.authenticate(id, pw)) {
            errorLabel.setText("Invalid Customer ID or password.");
            pwField.clear();
            return;
        }
        try {
            Customer customer = AppContext.bankService.findCustomer(id);
            new CustomerDashboard(stage, customer).show();
        } catch (AccountNotFoundException e) {
            errorLabel.setText("Customer not found.");
        }
    }
}
