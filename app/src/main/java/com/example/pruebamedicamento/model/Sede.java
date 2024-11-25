package com.example.pruebamedicamento.model;

public class Sede {
    private long id;
    private String direccion;
    private double latitud;
    private double longitud;
    private long franquiciaId;
    private long ciudadId;

    public Sede(long id, String direccion, double latitud, double longitud, long franquiciaId, long ciudadId) {
        this.id = id;
        this.direccion = direccion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.franquiciaId = franquiciaId;
        this.ciudadId = ciudadId;
    }

    // Getters
    public long getId() { return id; }
    public String getDireccion() { return direccion; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public long getFranquiciaId() { return franquiciaId; }
    public long getCiudadId() { return ciudadId; }
}



