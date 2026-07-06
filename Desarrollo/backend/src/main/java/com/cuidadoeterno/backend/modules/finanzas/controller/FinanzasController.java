package com.cuidadoeterno.backend.modules.finanzas.controller;
import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayCommitResponseDTO;
import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayInitRequestDTO;
import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayInitResponseDTO;
import com.cuidadoeterno.backend.modules.finanzas.service.FinanzasService;

// 1. IMPORTAMOS TU CLASE DE RESPUESTA PERSONALIZADA
import com.cuidadoeterno.backend.shared.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/finanzas/webpay")
@RequiredArgsConstructor
@Tag(name = "Finanzas - Webpay", description = "Integración con Transbank Webpay Plus")
public class FinanzasController {

    private final FinanzasService finanzasService;

    @Operation(summary = "Iniciar pago", description = "Genera el token y la URL para redirigir a Webpay")
    @ApiResponses({
        // 2. USAMOS LA RUTA COMPLETA PARA EL APIRESPONSE DE SWAGGER PARA EVITAR EL CHOQUE
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Transacción iniciada"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "502", description = "Error con Transbank")
    })
    @PostMapping("/iniciar")
    public ResponseEntity<ApiResponse<WebpayInitResponseDTO>> iniciarPago(@RequestBody WebpayInitRequestDTO request) {
        try {
            WebpayInitResponseDTO response = finanzasService.iniciarPagoWebpay(request);
            // 3. ENVUELTO CORRECTAMENTE
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.ok("Transacción iniciada en Webpay", response));
                    
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(ApiResponse.error("No se pudo conectar con Transbank: " + e.getMessage()));
        }
    }

    @Operation(summary = "Confirmar pago", description = "Valida el resultado en la base de datos.")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Pago procesado"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Token inválido"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Error al confirmar")
    })
    @GetMapping("/confirmar")
    public ResponseEntity<ApiResponse<WebpayCommitResponseDTO>> confirmarPago(
            @Parameter(description = "Token enviado por Transbank en la redirección")
            @RequestParam(value = "token_ws", required = false) String tokenWs) {

        if (tokenWs == null || tokenWs.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Pago cancelado o abandonado por el usuario"));
        }

        try {
            WebpayCommitResponseDTO response = finanzasService.confirmarPagoWebpay(tokenWs);
            return ResponseEntity.ok(ApiResponse.ok("Pago confirmado con éxito", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(ApiResponse.error("Error al confirmar el pago con Transbank: " + e.getMessage()));
        }
    }
}