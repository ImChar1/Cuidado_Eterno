package com.cuidadoeterno.backend.modules.cementerio.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class FallecidoDTO {
    private Integer idFallecido;
    private String nombres;
    private String apellidos;
    private LocalDate fechaDeNacimiento;
    private LocalDate fechaDefuncion;
    
    // Solo necesitamos el ID del espacio para vincularlo a una tumba existente
    private Integer idEspacio; 
}