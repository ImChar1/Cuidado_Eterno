package com.cuidadoeterno.backend.modules.finanzas.service;

import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayCommitResponseDTO;
import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayInitRequestDTO;
import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayInitResponseDTO;

public interface FinanzasService {
    
    /**
     * Inicia una transacción con Transbank y prepara la base de datos.
     * Devuelve el Token y la URL a la que la app debe redirigir al usuario.
     */
    WebpayInitResponseDTO iniciarPagoWebpay(WebpayInitRequestDTO request);

    /**
     * Transbank redirige de vuelta al backend con un token.
     * Este método confirma si el banco aprobó o rechazó los fondos.
     */
    WebpayCommitResponseDTO confirmarPagoWebpay(String tokenWs);
}