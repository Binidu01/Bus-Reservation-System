package com.example.busreservationsystem.controllers;

import com.example.busreservationsystem.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class RegisterBusController {

    @FXML private TextField busNumberField;
    @FXML private TextField totalSeatField;
    @FXML private TextField startPointField;
    @FXML private TextField endPointField;
    @FXML private TextField startTimeField;
    @FXML private TextField fareField;

    @FXML
    private void registerBus() {
        String busNumber = busNumberField.getText().trim();
        String totalSeat = totalSeatField.getText().trim();
        String startPoint = startPointField.getText().trim();
        String endPoint = endPointField.getText().trim();
        String startTime = startTimeField.getText().trim();
        String fare = fareField.getText().trim();

        if (busNumber.isEmpty() || totalSeat.isEmpty() || startPoint.isEmpty()
                || endPoint.isEmpty() || startTime.isEmpty() || fare.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "❌ Please fill in all fields.");
            return;
        }

        // ✅ Validate time: must be in HH:mm and between 00:00 and 23:59
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        try {
            LocalTime time = LocalTime.parse(startTime, formatter);

            // No need to check range explicitly: LocalTime.parse will fail on invalid ranges like 24:00 or 25:30
        } catch (DateTimeParseException e) {
            showAlert(Alert.AlertType.ERROR, "❌ Invalid time format. Please use HH:mm (e.g., 09:30 or 23:45).");
            return;
        }

        // ✅ Proceed with DB insert
        String sql = "INSERT INTO buses (bus_number, total_seats, start_point, end_point, start_time, fare) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, busNumber);
            stmt.setInt(2, Integer.parseInt(totalSeat));
            stmt.setString(3, startPoint);
            stmt.setString(4, endPoint);
            stmt.setString(5, startTime); // already validated
            stmt.setDouble(6, Double.parseDouble(fare));

            stmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "✅ Bus registered successfully!");
            clearFields();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "❌ Error: " + e.getMessage());
        }
    }

    private void clearFields() {
        busNumberField.clear();
        totalSeatField.clear();
        startPointField.clear();
        endPointField.clear();
        startTimeField.clear();
        fareField.clear();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle("Bus Registration");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
