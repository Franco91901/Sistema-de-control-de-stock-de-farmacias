package com.proyecto.dto;

public class StockPorSedeDto {
    private Integer idSede;
    private String nombre;
    private Integer stockTotal;

    // Constructor
    public StockPorSedeDto(Integer idSede, String nombre, Integer stockTotal) {
        this.idSede = idSede;
        this.nombre = nombre;
        this.stockTotal = stockTotal;
    }

    // Getters y Setters
    public Integer getIdSede() { return idSede; }
    public void setIdSede(Integer idSede) { this.idSede = idSede; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStockTotal() { return stockTotal; }
    public void setStockTotal(Integer stockTotal) { this.stockTotal = stockTotal; }
}
