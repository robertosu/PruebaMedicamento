package com.example.pruebamedicamento;

public class PointModel {
    private long id;
    private double latitude;
    private double longitude;
    private String title;

    public PointModel(long id, double latitude, double longitude, String title) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
    }

    // Getters y setters
    public long getId() { return id; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getTitle() { return title; }
}
