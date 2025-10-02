package com.example.busreservationsystem.controllers;

import com.example.busreservationsystem.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterCustomerController {

    @FXML private TextField nameField;
    @FXML private TextField mobileField;
    @FXML private TextField emailField;
    @FXML private TextField cityField;
    @FXML private TextField ageField;
    @FXML private PasswordField passwordField;

    @FXML
    private void registerCustomer() {
        String name = nameField.getText().trim();
        String mobile = mobileField.getText().trim();
        String email = emailField.getText().trim();
        String city = cityField.getText().trim();
        String ageText = ageField.getText().trim();
        String password = passwordField.getText().trim();

        String validationError = validateInputs(name, mobile, email, city, ageText, password);
        if (validationError != null) {
            showAlert(Alert.AlertType.ERROR, validationError);
            return;
        }

        int age = Integer.parseInt(ageText); // safe after validation
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

        String checkEmailSql = "SELECT COUNT(*) FROM customers WHERE email = ?";
        String insertSql = "INSERT INTO customers (name, mobile, email, city, age, password) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "❌ Database connection failed.");
                return;
            }

            // Check if email already exists
            try (PreparedStatement checkStmt = conn.prepareStatement(checkEmailSql)) {
                checkStmt.setString(1, email);
                try (var rs = checkStmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        showAlert(Alert.AlertType.ERROR, "❌ Email is already registered. Please use a different email.");
                        return;  // stop registration
                    }
                }
            }

            // Insert new customer
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, name);
                insertStmt.setString(2, mobile);
                insertStmt.setString(3, email);
                insertStmt.setString(4, city);
                insertStmt.setInt(5, age);
                insertStmt.setString(6, hashedPassword);

                int rows = insertStmt.executeUpdate();
                if (rows > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "✅ Customer registered successfully!");
                    clearFields();
                } else {
                    showAlert(Alert.AlertType.ERROR, "❌ Registration failed. Please try again.");
                }
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Database error: " + e.getMessage());
        }
    }

    private String validateInputs(String name, String mobile, String email, String city, String ageText, String password) {
        if (name.isEmpty() || mobile.isEmpty() || email.isEmpty() || city.isEmpty() || ageText.isEmpty() || password.isEmpty()) {
            return "Please fill in all fields including password.";
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$")) {
            return "Please enter a valid email address.";
        }

        if (!mobile.matches("^\\d{10}$")) {
            return "Mobile number must be exactly 10 digits.";
        }

        try {
            int age = Integer.parseInt(ageText);
            if (age <= 0 || age > 120) {
                return "Age must be a number between 1 and 120.";
            }
        } catch (NumberFormatException e) {
            return "Age must be a valid number.";
        }

        return null; // all valid
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Customer Registration");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearFields() {
        nameField.clear();
        mobileField.clear();
        emailField.clear();
        cityField.clear();
        ageField.clear();
        passwordField.clear();
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene loginScene = new Scene(loader.load(), 800, 600);

            Stage currentStage = (Stage) nameField.getScene().getWindow();
            currentStage.setTitle("Bus Reservation - Login");
            currentStage.setScene(loginScene);
            currentStage.setResizable(false);
            currentStage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Failed to load login screen: " + e.getMessage());
        }
    }
}
