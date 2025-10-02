package com.example.busreservationsystem.helpers;

public class LoggedCustomerSession {
    private static int customerId = -1;
    private static String customerName = "";

    public static void setCustomerId(int id) {
        customerId = id;
    }

    public static int getCustomerId() {
        return customerId;
    }

    public static void setCustomerName(String name) {
        customerName = name;
    }

    public static String getCustomerName() {
        return customerName;
    }

    public static void clearSession() {
        customerId = -1;
        customerName = "";
    }
}
