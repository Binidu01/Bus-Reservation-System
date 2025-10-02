package com.example.busreservationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/loading.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("Bus Reservation - Loading");
        stage.setScene(scene);
        stage.setResizable(false); // Set to true if you want resizing
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
