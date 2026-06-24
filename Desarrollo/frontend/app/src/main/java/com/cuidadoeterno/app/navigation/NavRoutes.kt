package com.cuidadoeterno.app.navigation
object NavRoutes {
    // ── Autenticación ───────────────────────────────────────────────────────────
    const val LOGIN               = "login"
    const val SELECCION_REGISTRO  = "seleccion_registro"
    const val REGISTRO_CLIENTE    = "registro_cliente"
    const val REGISTRO_CUIDADOR   = "registro_cuidador"

    // ── Homes por rol ───────────────────────────────────────────────────────────
    const val HOME_CLIENTE        = "home_cliente"
    const val HOME_CUIDADOR       = "home_cuidador"
    const val HOME_ADMIN          = "home_admin"

    // ── Perfil (compartido entre roles) ────────────────────────────────────────
    const val PERFIL              = "perfil"

    // ── Módulo Servicio — Grafo Anidado (Wizard de 4 pasos) ────────────────────
    const val FLUJO_SOLICITUD     = "flujo_solicitud"
    const val STEP_SERVICIOS      = "step_servicios"
    const val STEP_DATOS_ESPACIO  = "step_datos_espacio"
    const val STEP_CATALOGO       = "step_catalogo"
    const val STEP_RESUMEN        = "step_resumen"

    // ── Módulo Finanzas (Pago y Billetera) ─────────────────────────────────────
    const val PAGO_WEBPAY         = "pago_webpay/{url}/{token}"
    const val CONFIRMACION_PAGO   = "confirmacion_pago/{esExitoso}/{numOrden}/{codAuth}"
    const val BILLETERA_CUIDADOR  = "billetera_cuidador" // <-- NUEVA

    // ── Módulo Servicio — Flujos Dinámicos ──────────────────────────────────────
    const val DETALLE_SOLICITUD   = "detalle_solicitud/{idOrden}" // Cliente ve en vivo
    const val CALIFICACION        = "calificacion/{idOrden}"      // Cliente evalúa
    const val DETALLE_CUIDADOR    = "detalle_cuidador"            // Cuidador ejecuta

    // ── Historiales ─────────────────────────────────────────────────────────────
    const val HISTORIAL_CLIENTE   = "historial_cliente"
    const val HISTORIAL_CUIDADOR  = "historial_cuidador"

    // ── Helpers para construir rutas con parámetros ─────────────────────────────
    fun detalleSolicitud(idOrden: Int) = "detalle_solicitud/$idOrden"
    fun calificacion(idOrden: Int) = "calificacion/$idOrden"

    fun pagoWebpay(url: String, token: String): String {
        val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
        return "pago_webpay/$encodedUrl/$token"
    }

    fun confirmacionPago(esExitoso: Boolean, numOrden: String, codAuth: String) =
        "confirmacion_pago/$esExitoso/$numOrden/$codAuth"
}