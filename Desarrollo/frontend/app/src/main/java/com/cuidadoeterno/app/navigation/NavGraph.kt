package com.cuidadoeterno.app.navigation

import com.cuidadoeterno.app.modules.usuario.ui.login.LoginScreen

//Define todas las rutas
// Ejemplo de cómo se usará en NavGraph más adelante:
    composable("login") {
        LoginScreen()  { }en(
            viewModel = loginViewModel,
            onLoginExitoso = { rol ->
                when (rol) {
                    "CLIENTE"       -> navController.navigate("home_cliente")
                    "CUIDADOR"      -> navController.navigate("home_cuidador")
                    "ADMINISTRADOR" -> navController.navigate("home_admin")
                }
            },
            onIrARegistro = {
                navController.navigate("seleccion_registro")
            }
        )
    }