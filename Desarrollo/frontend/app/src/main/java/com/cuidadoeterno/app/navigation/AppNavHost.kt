package com.cuidadoeterno.app.navigation

//NavHost con el grafo completo

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cuidadoeterno.app.core.network.ApiClient
import com.cuidadoeterno.app.core.network.AuthInterceptor
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.core.session.dataStore
import com.cuidadoeterno.app.modules.cementerio.data.remote.CementerioApiService
import com.cuidadoeterno.app.modules.cementerio.data.repository.CementerioRepository
import com.cuidadoeterno.app.modules.servicio.data.remote.ServicioApiService
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository
import com.cuidadoeterno.app.modules.servicio.ui.home.HomeClienteScreen
import com.cuidadoeterno.app.modules.servicio.ui.home.HomeClienteViewModel
import com.cuidadoeterno.app.modules.servicio.ui.home.HomeCuidadorScreen
import com.cuidadoeterno.app.modules.servicio.ui.home.HomeCuidadorViewModel
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

    // ── Construcción de dependencias ────────────────────────────────────────────
    // Manual por ahora — cuando integres Hilt esto desaparece
    val sessionManager    = SessionManager(context)
    val authInterceptor   = AuthInterceptor(sessionManager)
    val retrofit          = ApiClient.createRetrofit(authInterceptor)

    val authApiService      = retrofit.create(AuthApiService::class.java)
    val cementerioApiService = retrofit.create(CementerioApiService::class.java)
    val servicioApiService  = retrofit.create(ServicioApiService::class.java)

    val authRepository      = AuthRepository(authApiService, sessionManager)
    val cementerioRepository = CementerioRepository(cementerioApiService)
    val servicioRepository  = ServicioRepository(servicioApiService)

    // ── Navegación ──────────────────────────────────────────────────────────────
    NavHost(
        navController = navController,
        startDestination = NavRoutes.LOGIN
    ) {

        // ── Login ───────────────────────────────────────────────────────────────
        composable(NavRoutes.LOGIN) {
            LoginScreen(
                viewModel = LoginViewModel(authRepository),
                onLoginExitoso = { rol ->
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

        // ── Selección tipo de registro ──────────────────────────────────────────
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

        // ── Home Cliente ────────────────────────────────────────────────────────
        composable(NavRoutes.HOME_CLIENTE) {
            HomeClienteScreen(
                viewModel = HomeClienteViewModel(servicioRepository, sessionManager),
                onNuevaSolicitud = { navController.navigate(NavRoutes.CREAR_SOLICITUD) },
                onVerHistorial   = { navController.navigate(NavRoutes.HISTORIAL_CLIENTE) },
                onVerPerfil      = { navController.navigate(NavRoutes.PERFIL) },
                onCerrarSesion   = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Home Cuidador ───────────────────────────────────────────────────────
        composable(NavRoutes.HOME_CUIDADOR) {
            HomeCuidadorScreen(
                viewModel = HomeCuidadorViewModel(
                    servicioRepository,
                    cementerioRepository,
                    sessionManager
                ),
                onVerHistorial   = { navController.navigate(NavRoutes.HISTORIAL_CUIDADOR) },
                onVerPagos       = { /* pendiente módulo finanzas */ },
                onVerPerfil      = { navController.navigate(NavRoutes.PERFIL) },
                onCerrarSesion   = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // ── Home Admin — placeholder hasta desarrollar panel admin ──────────────
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

        // ── Crear solicitud ─────────────────────────────────────────────────────
        // Pantalla pendiente de desarrollo — placeholder temporal
        composable(NavRoutes.CREAR_SOLICITUD) {
            // CrearSolicitudScreen — se implementa en el siguiente paso
        }

        // ── Detalle solicitud con parámetro idOrden ─────────────────────────────
        composable(
            route = NavRoutes.DETALLE_SOLICITUD,
            arguments = listOf(
                navArgument("idOrden") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val idOrden = backStackEntry.arguments?.getInt("idOrden") ?: 0
            // DetalleSolicitudScreen — se implementa en el siguiente paso
        }

        // ── Historial cliente ───────────────────────────────────────────────────
        // Pendiente de desarrollo
        composable(NavRoutes.HISTORIAL_CLIENTE) {
            // HistorialClienteScreen — se implementa después
        }

        // ── Historial cuidador ──────────────────────────────────────────────────
        composable(NavRoutes.HISTORIAL_CUIDADOR) {
            // HistorialCuidadorScreen — se implementa después
        }
    }
}