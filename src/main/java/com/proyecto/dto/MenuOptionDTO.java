// com.proyecto.dto.MenuOptionDTO
package com.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuOptionDTO {
    private String label;
    private String icon;
    private String url;
    private List<String> allowedRoles;
}