package com.example.busreservationsystem.controllers;

import com.example.busreservationsystem.DatabaseConnection;
import com.example.busreservationsystem.helpers.LoggedCustomerSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "⚠️ Please enter both email and password.");
            return;
        }

        String sql = "SELECT id, name, password FROM customers WHERE email = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                showAlert(Alert.AlertType.ERROR, "❌ Database connection failed.");
                return;
            }

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String hashedPassword = rs.getString("password");

                        if (BCrypt.checkpw(password, hashedPassword)) {
                            int id = rs.getInt("id");
                            String name = rs.getString("name");

                            // Set the logged-in customer session
                            LoggedCustomerSession.setCustomerId(id);

                            openCustomerDashboard(id, name);
                        } else {
                            showAlert(Alert.AlertType.ERROR, "❌ Invalid password.");
                        }
                    } else {
                        showAlert(Alert.AlertType.ERROR, "❌ User not found.");
                    }
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Database error: " + e.getMessage());
        }
    }

    private void openCustomerDashboard(int id, String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/customer_dashboard.fxml"));
            Scene scene = new Scene(loader.load(), 800, 600);

            CustomerDashboardController controller = loader.getController();
            controller.setCustomerData(id, name);

            Stage stage = new Stage();
            stage.setTitle("Customer Dashboard");
            stage.setScene(scene);
            stage.show();

            // Close login window
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Error loading dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register_customer.fxml"));
            Scene scene = new Scene(loader.load(), 900, 700);
            Stage stage = new Stage();
            stage.setTitle("Bus Reservation - Register Customer");
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Failed to load registration screen: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Login");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}