package com.example.busreservationsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/bus_system?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root"; // Update if your MAMP password is different

    public static Connection connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load JDBC driver
            Connection conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println("✅ Connected to MySQL database successfully!");
            return conn;
        } catch (ClassNotFoundException e) {
            System.err.println("❌ JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to MySQL database:");
            System.err.println("Error: " + e.getMessage());
        }
        return null;
    }

}
