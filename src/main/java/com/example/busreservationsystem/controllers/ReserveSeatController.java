package com.example.busreservationsystem.controllers;

import com.example.busreservationsystem.DatabaseConnection;
import com.example.busreservationsystem.helpers.LoggedCustomerSession;
import com.example.busreservationsystem.models.Bus;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReserveSeatController {

    @FXML
    private Label busInfoLabel;

    @FXML
    private GridPane seatGrid;

    private Bus selectedBus;

    private final Image seatAvailableImage = new Image(getClass().getResourceAsStream("/images/seat_available.png"));
    private final Image seatReservedImage = new Image(getClass().getResourceAsStream("/images/seat_reserved.png"));

    // Stack for GUI (undo-like task view)
    private final Stack<Task> taskStack = new Stack<>();

    // CLI Queue (FIFO order)
    private final Queue<Task> reservationQueue = new LinkedList<>();

    public void setSelectedBus(Bus bus) {
        this.selectedBus = bus;
        updateUI();
        loadSeats();
    }

    private void updateUI() {
        if (selectedBus != null) {
            String info = String.format(
                    "Bus: %s | From: %s To: %s | Departure: %s | Fare: %.2f",
                    selectedBus.getBusNumber(),
                    selectedBus.getStartPoint(),
                    selectedBus.getEndPoint(),
                    selectedBus.getStartTime(),
                    selectedBus.getFare()
            );
            busInfoLabel.setText(info);
        } else {
            busInfoLabel.setText("No bus selected");
        }
    }

    private void loadSeats() {
        seatGrid.getChildren().clear();

        if (selectedBus == null) return;

        int totalSeats = selectedBus.getTotalSeats();
        Set<Integer> reservedSeats = loadReservedSeats(selectedBus.getId());

        int seatsPerRow = 4;
        int rows = (int) Math.ceil(totalSeats / (double) seatsPerRow);

        int seatNumber = 1;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < seatsPerRow; col++) {
                if (seatNumber > totalSeats) break;

                int gridCol = col < 2 ? col : col + 1;
                final int currentSeat = seatNumber;

                javafx.scene.layout.VBox seatBox = new javafx.scene.layout.VBox(2);
                seatBox.setPrefWidth(40);
                seatBox.setPrefHeight(60);
                seatBox.setStyle("-fx-alignment: center;");

                ImageView seatIcon = new ImageView();
                seatIcon.setFitWidth(40);
                seatIcon.setFitHeight(40);

                Label seatNumberLabel = new Label(String.valueOf(currentSeat));
                seatNumberLabel.setStyle("-fx-font-size: 11;");

                if (reservedSeats.contains(currentSeat)) {
                    seatIcon.setImage(seatReservedImage);
                    seatIcon.setOpacity(0.5);
                    seatIcon.setDisable(true);
                    seatNumberLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                } else {
                    seatIcon.setImage(seatAvailableImage);
                    seatIcon.setOpacity(1.0);
                    seatNumberLabel.setStyle("-fx-text-fill: green;");

                    seatIcon.setOnMouseClicked(e -> {
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Reserve Seat");
                        alert.setHeaderText(null);
                        alert.setContentText("Do you want to reserve seat number " + currentSeat + "?");

                        Optional<ButtonType> result = alert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            boolean success = reserveSeat(selectedBus.getId(), currentSeat);

                            if (success) {
                                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                                successAlert.setTitle("Success");
                                successAlert.setHeaderText(null);
                                successAlert.setContentText("Seat " + currentSeat + " reserved successfully!");
                                successAlert.showAndWait();
                                loadSeats();
                            } else {
                                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                                errorAlert.setTitle("Error");
                                errorAlert.setHeaderText(null);
                                errorAlert.setContentText("Failed to reserve seat. Please try again.");
                                errorAlert.showAndWait();
                            }
                        }
                    });
                }

                seatBox.getChildren().addAll(seatIcon, seatNumberLabel);
                seatGrid.add(seatBox, gridCol, row);

                seatNumber++;
            }
        }
    }

    private Set<Integer> loadReservedSeats(int busId) {
        Set<Integer> reserved = new HashSet<>();
        String sql = "SELECT seat_number FROM reservations WHERE bus_id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, busId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reserved.add(rs.getInt("seat_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reserved;
    }

    private boolean reserveSeat(int busId, int seatNumber) {
        int customerId = getCurrentCustomerId();
        if (customerId == -1) return false;

        String sql = "INSERT INTO reservations (bus_id, seat_number, customer_id, status) VALUES (?, ?, ?, 'reserved')";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, busId);
            stmt.setInt(2, seatNumber);
            stmt.setInt(3, customerId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                String customerName = LoggedCustomerSession.getCustomerName();
                String busNumber = selectedBus != null ? selectedBus.getBusNumber() : "Unknown";

                Task task = new Task(customerName, busNumber, seatNumber, "RESERVATION");

                taskStack.push(task); // GUI stack
                reservationQueue.offer(task); // CLI FIFO queue

                // CLI output
                System.out.println("\n--- Reservation Queue (FIFO Order) ---");
                for (Task t : reservationQueue) {
                    System.out.println(t);
                }
            }

            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getCurrentCustomerId() {
        int id = LoggedCustomerSession.getCustomerId();
        if (id == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Required");
            alert.setHeaderText(null);
            alert.setContentText("You must be logged in to reserve a seat.");
            alert.showAndWait();
        }
        return id;
    }

    public void loadBusByNumber(String busNumber) {
        String sql = "SELECT * FROM buses WHERE bus_number = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, busNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String startTimeStr = null;
                java.sql.Time sqlTime = rs.getTime("start_time");
                if (sqlTime != null) {
                    startTimeStr = sqlTime.toLocalTime().toString();
                }

                selectedBus = new Bus(
                        rs.getInt("id"),
                        rs.getString("bus_number"),
                        rs.getInt("total_seat"),
                        rs.getString("start_point"),
                        rs.getString("end_point"),
                        startTimeStr,
                        rs.getDouble("fare")
                );

                updateUI();
                loadSeats();
            } else {
                System.out.println("Bus with number '" + busNumber + "' not found.");
                busInfoLabel.setText("Bus not found: " + busNumber);
                seatGrid.getChildren().clear();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ CLI method to process the reservation queue
    public void processReservationQueueCLI() {
        System.out.println("\n=== Processing Reservation Queue (FIFO) ===");
        while (!reservationQueue.isEmpty()) {
            Task task = reservationQueue.poll();
            System.out.println("Processing Task: " + task);

            // Simulated reservation processing
            System.out.println("✅ Seat " + task.seatNumber + " reserved for " + task.customerName + " on bus " + task.busNumber);
        }
        System.out.println("=== End of Queue ===\n");
    }

    // ✅ Inner class for Task
    static class Task {
        String customerName;
        String busNumber;
        int seatNumber;
        String type; // RESERVATION or CANCELLATION

        public Task(String customerName, String busNumber, int seatNumber, String type) {
            this.customerName = customerName;
            this.busNumber = busNumber;
            this.seatNumber = seatNumber;
            this.type = type;
        }

        @Override
        public String toString() {
            return type + " - Customer: " + customerName + ", Bus: " + busNumber + ", Seat: " + seatNumber;
        }
    }
}
