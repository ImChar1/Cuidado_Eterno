package com.cuidadoeterno.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument

import com.cuidadoeterno.app.core.network.ApiClient
import com.cuidadoeterno.app.core.network.AuthInterceptor
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.modules.cementerio.data.remote.CementerioApiService
import com.cuidadoeterno.app.modules.cementerio.data.repository.CementerioRepository
import com.cuidadoeterno.app.modules.finanzas.data.remote.FinanzasApiService
import com.cuidadoeterno.app.modules.finanzas.data.repository.FinanzasRepository
import com.cuidadoeterno.app.modules.finanzas.ui.pago.ConfirmacionPagoScreen
import com.cuidadoeterno.app.modules.servicio.data.remote.ServicioApiService
import com.cuidadoeterno.app.modules.servicio.data.repository.ServicioRepository

// Imports de UI (Asegúrate de importar tus nuevas pantallas y ViewModels)
import com.cuidadoeterno.app.modules.servicio.ui.cliente.home.HomeClienteScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.home.HomeClienteViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.DetalleSolicitudScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.DetalleSolicitudViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cliente.seguimiento.CalificacionScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.seguimiento.CalificacionViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.billetera.BilleteraScreen
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.billetera.BilleteraViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.detalle.DetalleOrdenCuidadorScreen
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.detalle.DetalleOrdenCuidadorViewModel
import com.cuidadoeterno.app.modules.informacion.ui.NosotrosScreen
import com.cuidadoeterno.app.modules.informacion.ui.FaqScreen // <-- IMPORT NUEVO
import com.cuidadoeterno.app.modules.servicio.ui.cliente.servicios.ServiciosScreen // <-- IMPORT NUEVO
import com.cuidadoeterno.app.modules.servicio.ui.cliente.servicios.ServiciosViewModel // <-- IMPORT NUEVO
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel // <-- IMPORT NUEVO
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.DatosEspacioScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.DatosEspacioViewModel

import com.cuidadoeterno.app.modules.usuario.data.remote.AuthApiService
import com.cuidadoeterno.app.modules.usuario.data.repository.AuthRepository
import com.cuidadoeterno.app.modules.usuario.ui.login.LoginScreen
import com.cuidadoeterno.app.modules.usuario.ui.login.LoginViewModel
import com.cuidadoeterno.app.modules.usuario.ui.registro.SeleccionRegistroScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cliente.RegistroClienteScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cliente.RegistroClienteViewModel
import com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador.RegistroCuidadorScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador.RegistroCuidadorViewModel
import com.cuidadoeterno.app.navigation.NavRoutes

@Composable
fun AppNavHost(navController: NavHostController) {

    val context = LocalContext.current

    // ── Construcción de dependencias ────────────────────────────────────────────
    val sessionManager    = SessionManager(context)
    val authInterceptor   = AuthInterceptor(sessionManager)
    val retrofit          = ApiClient.createRetrofit(authInterceptor)

    val authApiService       = retrofit.create(AuthApiService::class.java)
    val cementerioApiService = retrofit.create(CementerioApiService::class.java)
    val servicioApiService   = retrofit.create(ServicioApiService::class.java)
    val finanzasApiService   = retrofit.create(FinanzasApiService::class.java)

    val authRepository       = AuthRepository(authApiService, sessionManager)
    val cementerioRepository = CementerioRepository(cementerioApiService)
    val servicioRepository   = ServicioRepository(servicioApiService)
    val finanzasRepository   = FinanzasRepository(finanzasApiService)

    // En AppNavHost, ANTES del NavHost
    val token by sessionManager.authToken.collectAsStateWithLifecycle(initialValue = null)
    val rol by sessionManager.rol.collectAsStateWithLifecycle(initialValue = null)

    // Mientras carga el DataStore no sabemos si hay sesión
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(token) {
        cargando = false
    }

    if (cargando) {
        // Pantalla en blanco o splash mientras lee DataStore
        Box(Modifier.fillMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) }
        return
    }

    // startDestination dinámico según si hay sesión
    val startDestination = when {
        token.isNullOrEmpty() -> NavRoutes.LOGIN
        rol == "CLIENTE"      -> NavRoutes.HOME_CLIENTE
        rol == "CUIDADOR"     -> NavRoutes.HOME_CUIDADOR
        rol == "ADMINISTRADOR"-> NavRoutes.HOME_ADMIN
        else                  -> NavRoutes.LOGIN
    }

    // ── Navegación ──────────────────────────────────────────────────────────────
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // LOGIN
        composable(NavRoutes.LOGIN) {
            val viewModel: LoginViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        LoginViewModel(authRepository) as T
                }
            )
            LoginScreen(
                viewModel = viewModel,
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
                onIrARegistro = { navController.navigate(NavRoutes.SELECCION_REGISTRO) }
            )
        }

        // SELECCIÓN DE TIPO DE REGISTRO
        composable(NavRoutes.SELECCION_REGISTRO) {
            SeleccionRegistroScreen(
                onIrARegistroCliente  = { navController.navigate(NavRoutes.REGISTRO_CLIENTE) },
                onIrARegistroCuidador = { navController.navigate(NavRoutes.REGISTRO_CUIDADOR) },
                onVolver              = { navController.popBackStack() }
            )
        }

        // REGISTRO CLIENTE
        composable(NavRoutes.REGISTRO_CLIENTE) {
            val viewModel: RegistroClienteViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        RegistroClienteViewModel(authRepository) as T
                }
            )
            RegistroClienteScreen(
                viewModel         = viewModel,
                onRegistroExitoso = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SELECCION_REGISTRO) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // REGISTRO CUIDADOR
        composable(NavRoutes.REGISTRO_CUIDADOR) {
            val viewModel: RegistroCuidadorViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        RegistroCuidadorViewModel(authRepository) as T
                }
            )
            RegistroCuidadorScreen(
                viewModel         = viewModel,
                onRegistroExitoso = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.SELECCION_REGISTRO) { inclusive = true }
                    }
                },
                onVolver = { navController.popBackStack() }
            )
        }

        // =======================================================================
        // ── HOME CLIENTE ───────────────────────────────────────────────────────
        // =======================================================================
        composable(NavRoutes.HOME_CLIENTE) {
            val viewModel: HomeClienteViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        HomeClienteViewModel(sessionManager) as T
                }
            )

            HomeClienteScreen(
                viewModel = viewModel,
                onServiciosClick = { navController.navigate(NavRoutes.FLUJO_SOLICITUD) },
                onNosotrosClick = { navController.navigate("nosotros") },
                onFaqClick = { navController.navigate("faq") }, // <-- AHORA SÍ NAVEGA A FAQ
                onVerHistorial   = { navController.navigate(NavRoutes.HISTORIAL_CLIENTE) },
                onVerPerfil      = { navController.navigate(NavRoutes.PERFIL_CLIENTE) },
                onCerrarSesion   = {
                    navController.navigate(NavRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // =======================================================================
        // ── INFORMACIÓN: NOSOTROS Y PREGUNTAS FRECUENTES ───────────────────────
        // =======================================================================
        composable("nosotros") {
            NosotrosScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        composable("faq") { // <-- PANTALLA FAQ AGREGADA
            FaqScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // =======================================================================
        // ── CLIENTE: SEGUIMIENTO EN VIVO Y CALIFICACIÓN ────────────────────────
        // =======================================================================
        composable(
            route = NavRoutes.DETALLE_SOLICITUD,
            arguments = listOf(navArgument("idOrden") { type = NavType.IntType })
        ) { backStackEntry ->
            val idOrden = backStackEntry.arguments?.getInt("idOrden") ?: 0

            val viewModel: DetalleSolicitudViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        DetalleSolicitudViewModel(servicioRepository, sessionManager) as T
                }
            )

            DetalleSolicitudScreen(
                idOrden = idOrden,
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onCalificar = { id ->
                    navController.navigate(NavRoutes.calificacion(id))
                }
            )
        }

        composable(
            route = NavRoutes.CALIFICACION,
            arguments = listOf(navArgument("idOrden") { type = NavType.IntType })
        ) { backStackEntry ->
            val idOrden = backStackEntry.arguments?.getInt("idOrden") ?: 0

            val viewModel: CalificacionViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        CalificacionViewModel(servicioRepository) as T
                }
            )

            CalificacionScreen(
                idOrden = idOrden,
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onCalificacionExitosa = {
                    navController.navigate(NavRoutes.HOME_CLIENTE) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // =======================================================================
        // ── CUIDADOR: EJECUCIÓN DEL TRABAJO Y BILLETERA ────────────────────────
        // =======================================================================
        composable(NavRoutes.DETALLE_CUIDADOR) {
            val viewModel: DetalleOrdenCuidadorViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        DetalleOrdenCuidadorViewModel(servicioRepository, sessionManager) as T
                }
            )

            DetalleOrdenCuidadorScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.BILLETERA_CUIDADOR) {
            val viewModel: BilleteraViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        BilleteraViewModel(servicioRepository, sessionManager) as T
                }
            )

            BilleteraScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }

        // =======================================================================
        // ── GRAFO ANIDADO: WIZARD DE CREACIÓN DE SOLICITUD ─────────────────────
        // =======================================================================
        navigation(
            startDestination = NavRoutes.STEP_SERVICIOS,
            route = NavRoutes.FLUJO_SOLICITUD
        ) {
            // PASO 1: SELECCIÓN DEL SERVICIO
            composable(NavRoutes.STEP_SERVICIOS) { backStackEntry ->
                // Este truco permite que el SolicitudFlowViewModel viva durante todo el grafo anidado
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavRoutes.FLUJO_SOLICITUD)
                }
                val flowViewModel: SolicitudFlowViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            SolicitudFlowViewModel(servicioRepository) as T
                    }
                )

                val serviciosViewModel: ServiciosViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            ServiciosViewModel(servicioRepository) as T
                    }
                )

                ServiciosScreen(
                    viewModel = serviciosViewModel,
                    flowViewModel = flowViewModel,
                    onBack = { navController.popBackStack() },
                    onSiguiente = {
                        navController.navigate(NavRoutes.STEP_DATOS_ESPACIO)
                    }
                )
            }

            // PASO 2: UBICACIÓN DE LA SEPULTURA (La del 3er wireframe, la agregaremos luego)
            composable(NavRoutes.STEP_DATOS_ESPACIO) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(NavRoutes.FLUJO_SOLICITUD)
                }

                // Recuperamos el MISMO viewModel compartido del Paso 1
                val flowViewModel: SolicitudFlowViewModel = viewModel(
                    viewModelStoreOwner = parentEntry,
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            SolicitudFlowViewModel(servicioRepository) as T
                    }
                )

                val datosEspacioViewModel: DatosEspacioViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T =
                            DatosEspacioViewModel(servicioRepository) as T
                    }
                )

                DatosEspacioScreen(
                    viewModel = datosEspacioViewModel,
                    flowViewModel = flowViewModel,
                    onBack = { navController.popBackStack() },
                    onSiguiente = {
                        // navController.navigate(NavRoutes.STEP_CATALOGO) // El paso 3 que harás después
                    }
                )
            }
        }

        // ── Historiales (Implementación Futura) ────────────────────────────────
        composable(NavRoutes.HISTORIAL_CLIENTE) { }
        composable(NavRoutes.HISTORIAL_CUIDADOR) { }
    }
}