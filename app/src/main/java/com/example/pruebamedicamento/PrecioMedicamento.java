package com.example.pruebamedicamento;

public class PrecioMedicamento {
    private long id;
    private long medicamentoId;

    public long getFranquiciaId() {
        return franquiciaId;
    }

    public void setFranquiciaId(long franquiciaId) {
        this.franquiciaId = franquiciaId;
    }

    private long franquiciaId;
    private double precio;
    private String nombreMedicamento; // Para almacenar el nombre del medicamento
    private String nombreFranquicia;
    private String direccionSede;     // Para almacenar la dirección de la sede

    public PrecioMedicamento(long id, long medicamentoId, long franquiciaId, double precio) {
        this.id = id;
        this.medicamentoId = medicamentoId;
        this.franquiciaId = franquiciaId;
        this.precio = precio;
    }

    // Getters y setters
    public long getId() { return id; }
    public long getMedicamentoId() { return medicamentoId; }
    public long getSedeId() { return franquiciaId; }
    public double getPrecio() { return precio; }
    public String getNombreMedicamento() { return nombreMedicamento; }
    public void setNombreFranquicia(String nombreFranquicia){
        this.nombreFranquicia = nombreFranquicia;
    }
    public void setNombreMedicamento(String nombreMedicamento) { this.nombreMedicamento = nombreMedicamento; }

    public String getDireccionSede() {
        return direccionSede;
    }

    public void setDireccionSede(String direccionSede) {
        this.direccionSede = direccionSede;
    }
}