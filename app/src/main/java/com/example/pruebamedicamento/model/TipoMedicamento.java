package com.example.pruebamedicamento.model;

public class TipoMedicamento {
    private long id;
    private String nombre;
    private String descripcion;
    private String imagenUrl;

    public TipoMedicamento(long id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters
    public long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}