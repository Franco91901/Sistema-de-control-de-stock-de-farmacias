package com.proyecto.service;

public class FarmaceuticoConstants {
	 public static final int UMBRAL_BAJO_STOCK = 10;
	    public static final int DIAS_ALERTA_CADUCIDAD = 30;
	    public static final String TIPO_NOTIF_BAJO_STOCK = "BAJO_STOCK";
	    public static final String TIPO_NOTIF_PROXIMO_CADUCAR = "PROXIMO_CADUCAR";
	    public static final String ESTADO_NOTIF_PENDIENTE = "PENDIENTE";
	    public static final String ESTADO_NOTIF_ATENDIDA = "ATENDIDA";
	    
	    // Para vistas
	    public static final String VISTA_MEDICAMENTOS = "farmaceutico/medicamentos";
	    public static final String VISTA_LOTES = "farmaceutico/lotes";
	    public static final String VISTA_STOCK = "farmaceutico/stock";
	    public static final String VISTA_NOTIFICACIONES = "farmaceutico/notificaciones";
	    
	    private FarmaceuticoConstants() {
	        // Clase de constantes, no instanciable
	    }
}