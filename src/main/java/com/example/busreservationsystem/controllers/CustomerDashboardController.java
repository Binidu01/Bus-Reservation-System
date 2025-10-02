package com.example.busreservationsystem.controllers;

import com.example.busreservationsystem.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Stack;

public class CustomerDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label idLabel;

    @FXML
    private VBox reservationList;

    private int customerId;

    // Stack to store recently deleted reservations for terminal viewing
    private static final Stack<Integer> deletedReservationStack = new Stack<>();

    // ✅ Called after login to initialize dashboard
    public void setCustomerData(int id, String name) {
        this.customerId = id;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + name + "!");
        }
        if (idLabel != null) {
            idLabel.setText("Customer ID: " + id);
        }
        loadReservedSeats(id);
    }

    // ✅ Load all reserved seats for this customer - ENHANCED VERSION WITH CANCEL FUNCTIONALITY
    private void loadReservedSeats(int customerId) {
        if (reservationList != null) {
            reservationList.getChildren().clear();
        }

        // Get reservations with reservation ID for cancellation
        String sql = "SELECT r.id as reservation_id, r.seat_number, r.bus_id, r.status, " +
                "b.bus_number, b.start_point, b.end_point, b.start_time, b.fare " +
                "FROM reservations r " +
                "LEFT JOIN buses b ON r.bus_id = b.id " +
                "WHERE r.customer_id = ? AND LOWER(TRIM(r.status)) = 'reserved'";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            boolean hasReservations = false;
            int count = 0;

            System.out.println("Searching for reservations for customer ID: " + customerId);

            while (rs.next()) {
                hasReservations = true;
                count++;

                int reservationId = rs.getInt("reservation_id");
                int seatNumber = rs.getInt("seat_number");
                int busId = rs.getInt("bus_id");
                String status = rs.getString("status");
                String busNumber = rs.getString("bus_number");
                String startPoint = rs.getString("start_point");
                String endPoint = rs.getString("end_point");
                String startTime = rs.getString("start_time");
                double fare = rs.getDouble("fare");

                // Debug information
                System.out.println("Found reservation #" + count + ": ID " + reservationId + ", Bus ID " + busId + ", Seat " + seatNumber + ", Status: '" + status + "'");

                // Create reservation info card
                createReservationInfoCard(reservationId, seatNumber, busId, busNumber, startPoint, endPoint, startTime, fare, status);
            }

            System.out.println("Total reservations found: " + count);

            if (!hasReservations && reservationList != null) {
                Label noResLabel = new Label("You have no reserved seats.");
                noResLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14px; -fx-padding: 10px;");
                reservationList.getChildren().add(noResLabel);

                // Add debug information for troubleshooting
                addDebugInfo(customerId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Database Error",
                    "Failed to load reserved seats",
                    "Database error: " + e.getMessage());
        }
    }

    // ✅ Create detailed reservation info card with cancel button
    private void createReservationInfoCard(int reservationId, int seatNumber, int busId, String busNumber,
                                           String startPoint, String endPoint, String startTime, double fare, String status) {

        if (reservationList == null) {
            return;
        }

        // Create main container for the reservation card
        VBox cardContainer = new VBox(5);
        cardContainer.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-width: 1px; " +
                "-fx-border-radius: 5px; -fx-padding: 15px; -fx-margin: 5px;");

        // Title
        Label titleLabel = new Label("🚌 Reservation Details");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #495057;");

        // Reservation ID
        Label reservationIdLabel = new Label("Reservation ID: " + reservationId);
        reservationIdLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #6c757d;");

        // Seat information
        Label seatLabel = new Label("🪑 Seat Number: " + seatNumber);
        seatLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #28a745;");

        // Bus information
        VBox busInfoContainer = new VBox(3);
        if (busNumber != null && !busNumber.trim().isEmpty()) {
            Label busNumberLabel = new Label("🚍 Bus Number: " + busNumber);
            busNumberLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

            Label routeLabel = new Label("📍 Route: " +
                    (startPoint != null ? startPoint : "N/A") + " → " +
                    (endPoint != null ? endPoint : "N/A"));
            routeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

            Label timeLabel = new Label("🕐 Departure Time: " +
                    (startTime != null ? startTime : "N/A"));
            timeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #495057;");

            Label fareLabel = new Label("💰 Fare: Rs. " + String.format("%.2f", fare));
            fareLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

            busInfoContainer.getChildren().addAll(busNumberLabel, routeLabel, timeLabel, fareLabel);
        } else {
            Label busIdLabel = new Label("🚍 Bus ID: " + busId + " (Details not available)");
            busIdLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #dc3545;");
            busInfoContainer.getChildren().add(busIdLabel);
        }

        // Status
        Label statusLabel = new Label("📊 Status: " + (status != null ? status.toUpperCase() : "UNKNOWN"));
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #ffc107; -fx-font-weight: bold;");

        // Cancel button
        Button cancelButton = new Button("❌ Cancel Reservation");
        cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-padding: 8px 16px; -fx-border-radius: 4px; -fx-background-radius: 4px;");

        // Add hover effect
        cancelButton.setOnMouseEntered(e -> cancelButton.setStyle("-fx-background-color: #c82333; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8px 16px; " +
                "-fx-border-radius: 4px; -fx-background-radius: 4px;"));
        cancelButton.setOnMouseExited(e -> cancelButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 8px 16px; " +
                "-fx-border-radius: 4px; -fx-background-radius: 4px;"));

        // Cancel button action
        String busInfo = (busNumber != null && !busNumber.trim().isEmpty()) ? busNumber : "Bus ID " + busId;
        cancelButton.setOnAction(e -> cancelReservation(reservationId, seatNumber, busInfo));

        // Add separator line
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #dee2e6;");

        // Add all components to card
        cardContainer.getChildren().addAll(
                titleLabel, reservationIdLabel, separator, seatLabel, busInfoContainer, statusLabel, cancelButton
        );

        // Add card to main container
        reservationList.getChildren().add(cardContainer);
    }

    // ✅ Cancel reservation functionality
    private void cancelReservation(int reservationId, int seatNumber, String busInfo) {
        // Confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Cancel Reservation");
        confirmDialog.setHeaderText("Are you sure you want to cancel this reservation?");
        confirmDialog.setContentText("Seat " + seatNumber + " on " + busInfo + "\nThis action cannot be undone.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete from database
                String deleteSql = "DELETE FROM reservations WHERE id = ?";

                try (Connection conn = DatabaseConnection.connect();
                     PreparedStatement stmt = conn.prepareStatement(deleteSql)) {

                    stmt.setInt(1, reservationId);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Reservation " + reservationId + " cancelled successfully");

                        // Add to deleted reservations stack for terminal viewing
                        deletedReservationStack.push(reservationId);

                        showAlert(Alert.AlertType.INFORMATION, "Success",
                                "Reservation Cancelled",
                                "Your reservation for seat " + seatNumber + " has been cancelled.");

                        // Refresh the reservations list
                        loadReservedSeats(customerId);
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Cancellation Failed",
                                "Could not cancel the reservation. Please try again.");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.err.println("Error cancelling reservation: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Database Error",
                            "Cancellation Failed",
                            "Database error: " + e.getMessage());
                }
            }
        });
    }

    // Terminal method to view recently deleted reservation IDs
    public static void printDeletedReservations() {
        System.out.println("\n=== Recently Deleted Reservation IDs ===");
        if (deletedReservationStack.isEmpty()) {
            System.out.println("(No deletions yet)");
        } else {
            Stack<Integer> tempStack = new Stack<>();
            // Copy to temp stack to preserve original order
            while (!deletedReservationStack.isEmpty()) {
                tempStack.push(deletedReservationStack.pop());
            }
            // Print and restore original stack
            while (!tempStack.isEmpty()) {
                int reservationId = tempStack.pop();
                System.out.println("- Reservation ID: " + reservationId);
                deletedReservationStack.push(reservationId);
            }
        }
        System.out.println("========================================\n");
    }

    // ✅ Add debug information to help troubleshoot
    private void addDebugInfo(int customerId) {
        System.out.println("=== DEBUG INFORMATION ===");

        // Check if customer exists
        String customerCheckSql = "SELECT COUNT(*) as count FROM customers WHERE id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(customerCheckSql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int customerCount = rs.getInt("count");
                System.out.println("Customer exists check: " + (customerCount > 0 ? "YES" : "NO"));
            }
        } catch (SQLException e) {
            System.err.println("Error checking customer existence: " + e.getMessage());
        }

        // Check all reservations for this customer (regardless of status)
        String allReservationsSql = "SELECT * FROM reservations WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(allReservationsSql)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("All reservations for customer " + customerId + ":");
            boolean foundAny = false;
            while (rs.next()) {
                foundAny = true;
                System.out.println("- Reservation ID: " + rs.getInt("id") +
                        ", Bus ID: " + rs.getInt("bus_id") +
                        ", Seat: " + rs.getInt("seat_number") +
                        ", Status: '" + rs.getString("status") + "'");
            }
            if (!foundAny) {
                System.out.println("NO reservations found for customer " + customerId);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching all reservations: " + e.getMessage());
        }

        // Check if buses table exists and has data
        String busCheckSql = "SELECT COUNT(*) as count FROM buses";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(busCheckSql)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int busCount = rs.getInt("count");
                System.out.println("Total buses in database: " + busCount);
            }
        } catch (SQLException e) {
            System.err.println("Error checking buses table: " + e.getMessage());
        }

        // Check specific bus with ID 1
        String specificBusSql = "SELECT * FROM buses WHERE id = 1";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(specificBusSql)) {

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Bus ID 1 details: " + rs.getString("bus_number") +
                        ", Route: " + rs.getString("start_point") + " → " + rs.getString("end_point"));
            } else {
                System.out.println("Bus ID 1 NOT FOUND in buses table");
            }
        } catch (SQLException e) {
            System.err.println("Error checking bus ID 1: " + e.getMessage());
        }

        System.out.println("=== END DEBUG INFO ===");
    }

    // ✅ Add method to refresh reservations
    @FXML
    private void refreshReservations() {
        loadReservedSeats(customerId);
    }

    // ✅ Open Reserve Seat page from dashboard
    private void openReserveSeatPage(String busNumber) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reserve_seat.fxml"));
            Parent root = loader.load();

            ReserveSeatController controller = loader.getController();
            if (controller != null) {
                controller.loadBusByNumber(busNumber);
            } else {
                System.err.println("ReserveSeatController is null. Check fx:controller in FXML.");
            }

            Stage stage = new Stage();
            stage.setTitle("Reserve Seat - Bus " + busNumber);
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Loading Error",
                    "Unable to open Reserve Seat page",
                    "An error occurred while loading the seat reservation window.");
        }
    }

    // ✅ Navigate to Bus Search screen
    @FXML
    private void goToReserveSeat() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/bus_search.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Search Buses to Reserve Seat");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Unable to open Bus Search page",
                    "Something went wrong while navigating to the bus search.");
        }
    }

    // ✅ Navigate to Register New Bus screen (admin option)
    @FXML
    private void goToRegisterBus() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register_bus.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Add New Bus");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Unable to open Register Bus page",
                    "Something went wrong while navigating to the bus registration.");
        }
    }

    // ✅ Log out and go back to login screen
    @FXML
    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(new Scene(root, 800, 600));
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error",
                    "Unable to return to login",
                    "Something went wrong while logging out.");
        }
    }

    // ✅ Reusable alert method
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}