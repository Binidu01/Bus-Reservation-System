package com.example.busreservationsystem.controllers;

import com.example.busreservationsystem.DatabaseConnection;
import com.example.busreservationsystem.models.Bus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class BusSearchController {

    @FXML private TableView<Bus> busTableView;
    @FXML private TableColumn<Bus, String> busNumberCol;
    @FXML private TableColumn<Bus, Integer> seatsCol;
    @FXML private TableColumn<Bus, String> startPointCol;
    @FXML private TableColumn<Bus, String> endPointCol;
    @FXML private TableColumn<Bus, String> startTimeCol;
    @FXML private TableColumn<Bus, Double> fareCol;

    @FXML private TextField startPointField;
    @FXML private TextField endPointField;

    private ObservableList<Bus> busList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        busNumberCol.setCellValueFactory(new PropertyValueFactory<>("busNumber"));
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("totalSeats"));
        startPointCol.setCellValueFactory(new PropertyValueFactory<>("startPoint"));
        endPointCol.setCellValueFactory(new PropertyValueFactory<>("endPoint"));
        startTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime")); // as String
        fareCol.setCellValueFactory(new PropertyValueFactory<>("fare"));

        loadBusDataFromDatabase();
        busTableView.setItems(busList);

        // Double-click to open ReserveSeat page
        busTableView.setRowFactory(tv -> {
            TableRow<Bus> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 2) {
                    Bus clickedBus = row.getItem();
                    openReserveSeatPage(clickedBus);
                }
            });
            return row;
        });
    }

    private void loadBusDataFromDatabase() {
        busList.clear();

        String sql = "SELECT * FROM buses";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String busNumber = rs.getString("bus_number");
                int totalSeats = rs.getInt("total_seats");
                String startPoint = rs.getString("start_point");
                String endPoint = rs.getString("end_point");
                String startTime = rs.getString("start_time");
                double fare = rs.getDouble("fare");

                Bus bus = new Bus(id, busNumber, totalSeats, startPoint, endPoint, startTime, fare);
                busList.add(bus);
            }

        } catch (SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Could not load bus data");
            alert.setContentText("Please check your database connection or try again later.");
            alert.showAndWait();
        }
    }

    @FXML
    private void handleSearch() {
        String start = startPointField.getText().trim();
        String end = endPointField.getText().trim();

        if (start.isEmpty() || end.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Input Required");
            alert.setHeaderText("Missing Fields");
            alert.setContentText("Please enter both start and end points.");
            alert.showAndWait();
            return;
        }

        busList.clear();

        String sql = "SELECT * FROM buses WHERE start_point = ? AND end_point = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, start);
            stmt.setString(2, end);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String busNumber = rs.getString("bus_number");
                    int totalSeats = rs.getInt("total_seats");
                    String startPoint = rs.getString("start_point");
                    String endPoint = rs.getString("end_point");
                    String startTime = rs.getString("start_time");
                    double fare = rs.getDouble("fare");

                    Bus bus = new Bus(id, busNumber, totalSeats, startPoint, endPoint, startTime, fare);
                    busList.add(bus);
                }
            }

            if (busList.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("No Results");
                alert.setHeaderText("No Buses Found");
                alert.setContentText("No buses found for the given route.");
                alert.showAndWait();
            }

        } catch (SQLException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Search Failed");
            alert.setContentText("Could not perform the search. Please try again.");
            alert.showAndWait();
        }
    }

    private void openReserveSeatPage(Bus bus) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reserve_seat.fxml"));
            Parent root = loader.load();

            ReserveSeatController controller = loader.getController();
            controller.setSelectedBus(bus);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Reserve Seat - Bus " + bus.getBusNumber());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Loading Error");
            alert.setHeaderText("Unable to open Reserve Seat page");
            alert.setContentText("An error occurred while loading the reserve seat window.\nPlease try again.");
            alert.showAndWait();
        }
    }
}
