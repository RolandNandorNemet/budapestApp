package com.example.budapestapp.model;

public class Route {
    private String id;
    private String type;
    private String lineNumber;
    private String departureStation;
    private String destinationStation;
    private String departureTime;
    private String arrivalTime;

    public Route() {}

    public Route(String id, String type, String lineNumber,
                 String departureStation, String destinationStation,
                 String departureTime, String arrivalTime) {
        this.id = id;
        this.type = type;
        this.lineNumber = lineNumber;
        this.departureStation = departureStation;
        this.destinationStation = destinationStation;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLineNumber() { return lineNumber; }
    public void setLineNumber(String lineNumber) { this.lineNumber = lineNumber; }

    public String getDepartureStation() { return departureStation; }
    public void setDepartureStation(String departureStation) { this.departureStation = departureStation; }

    public String getDestinationStation() { return destinationStation; }
    public void setDestinationStation(String destinationStation) { this.destinationStation = destinationStation; }

    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }

    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
}