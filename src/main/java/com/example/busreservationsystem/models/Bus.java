package com.example.busreservationsystem.models;

public class Bus {
    private int id;
    private String busNumber;
    private int totalSeats;
    private String startPoint;
    private String endPoint;
    private String startTime; // can be LocalTime if you prefer
    private double fare;

    public Bus(int id, String busNumber, int totalSeats, String startPoint,
               String endPoint, String startTime, double fare) {
        this.id = id;
        this.busNumber = busNumber;
        this.totalSeats = totalSeats;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.startTime = startTime;
        this.fare = fare;
    }

    // Getters and setters
    public int getId() { return id; }
    public String getBusNumber() { return busNumber; }
    public int getTotalSeats() { return totalSeats; }
    public String getStartPoint() { return startPoint; }
    public String getEndPoint() { return endPoint; }
    public String getStartTime() { return startTime; }
    public double getFare() { return fare; }
}
