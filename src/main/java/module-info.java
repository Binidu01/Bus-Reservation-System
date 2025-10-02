module com.example.busreservationsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires mysql.connector.j;

    // Add this to include the jbcrypt library (if your IDE recognizes this module name)
    requires jbcrypt;

    // If jbcrypt is an unnamed module and not recognized, add this line to read unnamed modules:
    // (optional, sometimes helps with unnamed modules)
    // requires static org.mindrot.jbcrypt;

    // Required for FXMLLoader to access your controller classes
    opens com.example.busreservationsystem to javafx.fxml;
    opens com.example.busreservationsystem.controllers to javafx.fxml;

    // **Add this line to open the models package to javafx.base and javafx.fxml for reflection**
    opens com.example.busreservationsystem.models to javafx.base, javafx.fxml;

    // Export packages for access outside this module if needed
    exports com.example.busreservationsystem;
    exports com.example.busreservationsystem.controllers;
}
