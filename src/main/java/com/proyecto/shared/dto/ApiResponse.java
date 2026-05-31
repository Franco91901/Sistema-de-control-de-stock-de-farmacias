package com.proyecto.shared.dto;

public record ApiResponse<T>(
    boolean success,
    String mensaje,
    T data
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, null, data);
    }

    public static <T> ApiResponse<T> ok(String mensaje, T data) {
        return new ApiResponse<>(true, mensaje, data);
    }

    public static <T> ApiResponse<T> error(String mensaje) {
        return new ApiResponse<>(false, mensaje, null);
    }
}
