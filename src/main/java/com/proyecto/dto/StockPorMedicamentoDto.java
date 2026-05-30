package com.proyecto.dto;

public class StockPorMedicamentoDto {
    private Integer idMedicamento;
    private String nombre;
    private String nombreSede;
    private Integer stockTotal;

    // Constructor
    public StockPorMedicamentoDto(Integer idMedicamento, String nombre, String nombreSede, Integer stockTotal) {
        this.idMedicamento = idMedicamento;
        this.nombre = nombre;
        this.nombreSede = nombreSede;
        this.stockTotal = stockTotal;
    }

    // Getters y Setters
    public Integer getIdMedicamento() { return idMedicamento; }
    public void setIdMedicamento(Integer idMedicamento) { this.idMedicamento = idMedicamento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getNombreSede() { return nombreSede; }
    public void setNombreSede(String nombreSede) { this.nombreSede = nombreSede; }

    public Integer getStockTotal() { return stockTotal; }
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
}