package com.example.busreservationsystem.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadingController {

    private static final Logger logger = Logger.getLogger(LoadingController.class.getName());

    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;

    @FXML
    public void initialize() {
        // Start progress bar animation on a new thread
        new Thread(() -> {
            try {
                for (int i = 1; i <= 100; i++) {
                    Thread.sleep(30); // simulate loading
                    final int progress = i;
                    Platform.runLater(() -> progressBar.setProgress(progress / 100.0));
                }

                Platform.runLater(() -> {
                    try {
                        // Load main screen
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                        Scene scene = new Scene(loader.load(), 800, 550);
                        Stage stage = new Stage();
                        stage.setTitle("Bus Reservation - Login Customer");
                        stage.setScene(scene);
                        stage.show();

                        // Close loading screen
                        Stage current = (Stage) progressBar.getScene().getWindow();
                        current.close();

                    } catch (Exception e) {
                        logger.log(Level.SEVERE, "Error loading register_customer.fxml", e);
                        statusLabel.setText("❌ Failed to load. See logs.");
                    }
                });

            } catch (InterruptedException e) {
                logger.log(Level.WARNING, "Loading interrupted", e);
            }
        }).start();
    }
}
