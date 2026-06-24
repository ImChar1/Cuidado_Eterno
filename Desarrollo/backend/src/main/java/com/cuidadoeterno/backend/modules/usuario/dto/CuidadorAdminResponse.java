package com.cuidadoeterno.backend.modules.usuario.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CuidadorAdminResponse {
    private Integer idPersona;
    private String rut;
    private String nombre;
    private String apPaterno;
    private String email;
    private String telefono;
    private String estadoVerificacion;
    private LocalDate fechaRegistro;
}