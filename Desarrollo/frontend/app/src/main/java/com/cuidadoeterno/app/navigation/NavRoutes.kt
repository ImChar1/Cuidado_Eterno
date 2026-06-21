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

    // ── Módulo servicio — Cliente ───────────────────────────────────────────────
    const val CREAR_SOLICITUD     = "crear_solicitud"
    const val DETALLE_SOLICITUD   = "detalle_solicitud/{idOrden}"
    const val HISTORIAL_CLIENTE   = "historial_cliente"

    // ── Módulo servicio — Cuidador ──────────────────────────────────────────────
    const val HISTORIAL_CUIDADOR  = "historial_cuidador"

    // Helper para construir rutas con parámetros
    fun detalleSolicitud(idOrden: Int) = "detalle_solicitud/$idOrden"
}