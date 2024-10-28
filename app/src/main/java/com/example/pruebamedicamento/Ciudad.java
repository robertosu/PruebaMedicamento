package com.example.pruebamedicamento;

public class Ciudad {
    private long id;
    private String nombre;

    public Ciudad(long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}