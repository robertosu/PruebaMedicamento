package com.example.pruebamedicamento;

public class Franquicia {
    private long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;

    public Franquicia(long id, String nombre, String descripcion, String imagenUrl) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagenUrl = imagenUrl;
    }

    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getImagenUrl() { return imagenUrl; }
}