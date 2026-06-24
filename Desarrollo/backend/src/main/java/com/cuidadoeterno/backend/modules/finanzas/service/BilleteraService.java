package com.cuidadoeterno.backend.modules.finanzas.service;

import java.math.BigDecimal;

public interface BilleteraService {
    /**
     * Abona el pago de un servicio finalizado a la billetera del cuidador.
     */
    void abonarPagoPorServicio(Integer idCuidador, Integer idOrden, BigDecimal montoAbono);
}