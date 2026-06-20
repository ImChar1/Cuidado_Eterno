package com.cuidadoeterno.app.navigation

//Constantes de rutas ("login", "home")
object NavRoutes {
    const val LOGIN               = "login"
    const val SELECCION_REGISTRO  = "seleccion_registro"
    const val REGISTRO_CLIENTE    = "registro_cliente"
    const val REGISTRO_CUIDADOR   = "registro_cuidador"
    const val PERFIL              = "perfil"

    // Homes por rol — se expanden cuando agregues los otros módulos
    const val HOME_CLIENTE        = "home_cliente"
    const val HOME_CUIDADOR       = "home_cuidador"
    const val HOME_ADMIN          = "home_admin"
}