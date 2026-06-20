package com.cuidadoeterno.app.navigation

//NavHost con el grafo completo

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.cuidadoeterno.app.core.network.ApiClient
import com.cuidadoeterno.app.core.network.AuthInterceptor
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.core.session.dataStore
import com.cuidadoeterno.app.modules.usuario.data.remote.AuthApiService
import com.cuidadoeterno.app.modules.usuario.data.repository.AuthRepository
import com.cuidadoeterno.app.modules.usuario.ui.login.LoginScreen
import com.cuidadoeterno.app.modules.usuario.ui.login.LoginViewModel
import com.cuidadoeterno.app.modules.usuario.ui.perfil.PerfilScreen
import com.cuidadoeterno.app.modules.usuario.ui.perfil.PerfilViewModel
import com.cuidadoeterno.app.modules.usuario.ui.registro.SeleccionRegistroScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cliente.RegistroClienteScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cliente.RegistroClienteViewModel
import com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador.RegistroCuidadorScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador.RegistroCuidadorViewModel

@Composable
fun AppNavHost(navController: NavHostController) {

    val context = LocalContext.current

    // ── Construcción manual de dependencias ─────────────────────────────────────
    // Por ahora sin Hilt — instanciamos aquí directamente.
    // Cuando integres Hilt, esto desaparece y usas hiltViewModel().
    val sessionManager = SessionManager(context)
    val authInterceptor = AuthInterceptor(sessionManager)
    val retrofit = ApiClient.createRetrofit(authInterceptor)
    val authApiService = retrofit.create(AuthApiService::class.java)
    val authRepository = AuthRepository(authApiService, sessionManager)

    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {

        // ── Login ───────────────────────────────────────────────────────────────
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                viewModel = LoginViewModel(authRepository),
                onLoginExitoso = { rol ->
                    // Navegar según el rol y limpiar el backstack
                    // para que el botón atrás no vuelva al login
                    val destino = when (rol) {
                        "CLIENTE"       -> NavRoutes.HOME_CLIENTE
                        "CUIDADOR"      -> NavRoutes.HOME_CUIDADOR
                        "ADMINISTRADOR" -> NavRoutes.HOME_ADMIN
                        else            -> NavRoutes.HOME_CLIENTE
                    }
                    navController.navigate(destino) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onIrARegistro = {
                    navController.navigate(NavRoutes.SELECCION_REGISTRO)
                }
            )
        }

        // ── Selección de tipo de registro ───────────────────────────────────────
        composable(NavRoutes.SELECCION_REGISTRO) {
            SeleccionRegistroScreen(
                onIrARegistroCliente  = { navController.navigate(NavRoutes.REGISTRO_CLIENTE) },
                onIrARegistroCuidador = { navController.navigate(NavRoutes.REGISTRO_CUIDADOR) },
                onVolver              = { navController.popBackStack() }
            )
        }

        // ── Registro cliente ────────────────────────────────────────────────────
        composable(NavRoutes.REGISTRO_CLIENTE) {
            RegistroClienteScreen(
                viewModel = RegistroClienteViewModel(authRepository),
                onRegistroExitoso = {
                    // Registro exitoso → ir al login para que inicie sesión
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // ── Registro cuidador ───────────────────────────────────────────────────
        composable(NavRoutes.REGISTRO_CUIDADOR) {
            RegistroCuidadorScreen(
                viewModel = RegistroCuidadorViewModel(authRepository),
                onRegistroExitoso = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.LOGIN) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // ── Perfil ──────────────────────────────────────────────────────────────
        composable(NavRoutes.PERFIL) {
            PerfilScreen(
                viewModel = PerfilViewModel(authRepository),
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Homes temporales por rol ────────────────────────────────────────────
        // Placeholder hasta que desarrolles las pantallas de cada módulo.
        // Muestran el perfil del usuario como pantalla principal por ahora.
        composable(NavRoutes.HOME_CLIENTE) {
            PerfilScreen(
                viewModel = PerfilViewModel(authRepository),
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME_CUIDADOR) {
            PerfilScreen(
                viewModel = PerfilViewModel(authRepository),
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.HOME_ADMIN) {
            PerfilScreen(
                viewModel = PerfilViewModel(authRepository),
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}