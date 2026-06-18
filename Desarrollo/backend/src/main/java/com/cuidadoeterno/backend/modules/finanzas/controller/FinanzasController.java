package com.cuidadoeterno.backend.modules.finanzas.controller;

import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayCommitResponseDTO;
import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayInitRequestDTO;
import com.cuidadoeterno.backend.modules.finanzas.dto.WebpayInitResponseDTO;
import com.cuidadoeterno.backend.modules.finanzas.service.FinanzasService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/finanzas/webpay")
@RequiredArgsConstructor
@Tag(name = "Finanzas - Webpay", description = "Integración con Transbank Webpay Plus")
public class FinanzasController {

    private final FinanzasService finanzasService;

    /**
     * Inicia una transacción Webpay Plus.
     * POST /api/v1/finanzas/webpay/iniciar
     *
     * El frontend recibe el token y la URL, luego redirige al usuario a:
     * {url}?token_ws={token}
     */
    @Operation(summary = "Iniciar pago con Webpay Plus",
               description = "Crea la transacción en Transbank y retorna el token y URL de redirección al formulario de pago.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Transacción iniciada correctamente"),
        @ApiResponse(responseCode = "400", description = "Solicitud de servicio no encontrada o datos inválidos"),
        @ApiResponse(responseCode = "500", description = "Error de comunicación con Transbank")
    })
    @PostMapping("/iniciar")
    public ResponseEntity<?> iniciarPago(@RequestBody WebpayInitRequestDTO request) {
        try {
            WebpayInitResponseDTO response = finanzasService.iniciarPagoWebpay(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Solicitud no encontrada, o Webpay no configurado en BD
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            // Error de red o respuesta inesperada de Transbank
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "No se pudo conectar con Transbank", "detalle", e.getMessage()));
        }
    }

    /**
     * Confirma el resultado del pago.
     * GET /api/v1/finanzas/webpay/confirmar?token_ws=XXXXX
     *
     * Transbank redirige al usuario aquí vía GET después de que ingresa su tarjeta.
     * Si el usuario abandona el pago, Transbank envía token_ws vacío o ausente.
     */
    @Operation(summary = "Confirmar resultado del pago (callback de Transbank)",
               description = "Transbank redirige al usuario a este endpoint con el token_ws. " +
                             "Se llama al commit y se registra el resultado en la base de datos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Pago procesado (aprobado o rechazado por el banco)"),
        @ApiResponse(responseCode = "400", description = "Token inválido, ausente o transacción ya procesada"),
        @ApiResponse(responseCode = "500", description = "Error al confirmar con Transbank")
    })
    @GetMapping("/confirmar")
    public ResponseEntity<?> confirmarPago(
            @Parameter(description = "Token enviado por Transbank en la redirección")
            @RequestParam(value = "token_ws", required = false) String tokenWs) {

        // Transbank omite token_ws si el usuario abandonó el pago antes de completarlo
        if (tokenWs == null || tokenWs.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Pago cancelado o abandonado por el usuario"));
        }

        try {
            WebpayCommitResponseDTO response = finanzasService.confirmarPagoWebpay(tokenWs);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Token no encontrado en BD
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Error al confirmar el pago con Transbank", "detalle", e.getMessage()));
        }
    }
}