package com.cuidadoeterno.app.modules.servicio.data.model

data class SolicitudDraft(
    // 1. Tipo de Servicio Seleccionado
    var idTipoSolicitud: Int? = null,
    var nombreServicio: String? = null,
    var precioBase: Double = 0.0,

    // 2. Datos del Fallecido
    var nombreFallecido: String = "",
    var apellidoFallecido: String = "",

    // 3. Ubicación del Espacio (Sepultura)
    var idCementerio: Int? = null,
    var idTipoEspacio: Int? = null,
    var sectorPabellon: String = "",
    var pisoNivel: String? = null,
    var pasillo: String? = null,
    var numeroSepultura: String = "",

    // 4. Productos Adicionales Opcionales
    var productosAdicionales: List<InsumoSeleccionado> = emptyList(),

    // 5. Totales Consolidados (Para resumen de Transbank)
    var montoInsumos: Double = 0.0,
    var montoTotal: Double = 0.0
) {
    /**
     * Recalcula dinámicamente los montos de la orden cada vez que finaliza un paso.
     */
    fun actualizarTotales() {
        montoInsumos = productosAdicionales.sumOf { it.montoTotal }
        montoTotal = precioBase + montoInsumos
    }
}