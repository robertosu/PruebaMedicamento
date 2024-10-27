package com.example.pruebamedicamento;

public class PrecioMedicamento {
    private long id;
    private long medicamentoId;
    private long sedeId;
    private double precio;
    private String nombreMedicamento; // Para almacenar el nombre del medicamento
    private String direccionSede;     // Para almacenar la dirección de la sede

    public PrecioMedicamento(long id, long medicamentoId, long sedeId, double precio) {
        this.id = id;
        this.medicamentoId = medicamentoId;
        this.sedeId = sedeId;
        this.precio = precio;
    }

    // Getters y setters
    public long getId() { return id; }
    public long getMedicamentoId() { return medicamentoId; }
    public long getSedeId() { return sedeId; }
    public double getPrecio() { return precio; }
    public String getNombreMedicamento() { return nombreMedicamento; }
    public void setNombreMedicamento(String nombreMedicamento) { this.nombreMedicamento = nombreMedicamento; }
    public String getDireccionSede() { return direccionSede; }
    public void setDireccionSede(String direccionSede) { this.direccionSede = direccionSede; }
}