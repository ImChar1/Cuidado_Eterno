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
import com.cuidadoeterno.app.modules.servicio.data.remote.InventarioApiService
import com.cuidadoeterno.app.modules.servicio.data.repository.InventarioRepository
import com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo.CatalogoProductoScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo.CatalogoProductoViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo.CatalogoViewModelFactory
import com.cuidadoeterno.app.modules.servicio.ui.cliente.servicios.ServiciosScreen // <-- IMPORT NUEVO
import com.cuidadoeterno.app.modules.servicio.ui.cliente.servicios.ServiciosViewModel // <-- IMPORT NUEVO
import com.cuidadoeterno.app.modules.servicio.ui.cliente.flow.SolicitudFlowViewModel // <-- IMPORT NUEVO
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.DatosEspacioScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.DatosEspacioViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.PreguntaProductosScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.ResumenSolicitudScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.ResumenSolicitudViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.home.HomeCuidadorScreen
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.home.HomeCuidadorViewModel
import com.cuidadoeterno.app.modules.admin.data.remote.AdminApiService
import com.cuidadoeterno.app.modules.admin.data.repository.AdminRepository
import com.cuidadoeterno.app.modules.admin.ui.home.HomeAdminScreen
import com.cuidadoeterno.app.modules.admin.ui.cuidador.GestionCuidadoresScreen
import com.cuidadoeterno.app.modules.admin.ui.cuidador.GestionCuidadoresViewModel
import com.cuidadoeterno.app.modules.usuario.data.remote.AuthApiService
import com.cuidadoeterno.app.modules.usuario.data.repository.AuthRepository
import com.cuidadoeterno.app.modules.usuario.ui.login.LoginScreen
import com.cuidadoeterno.app.modules.usuario.ui.login.LoginViewModel
import com.cuidadoeterno.app.modules.usuario.ui.perfil.cliente.PerfilScreen
import com.cuidadoeterno.app.modules.usuario.ui.perfil.cliente.PerfilViewModel
import com.cuidadoeterno.app.modules.usuario.ui.registro.SeleccionRegistroScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cliente.RegistroClienteScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cliente.RegistroClienteViewModel
import com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador.RegistroCuidadorScreen
import com.cuidadoeterno.app.modules.usuario.ui.registro.cuidador.RegistroCuidadorViewModel
import android.net.Uri
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cuidadoeterno.app.modules.finanzas.ui.pago.PagoWebpayScreen
import com.cuidadoeterno.app.modules.finanzas.ui.pago.PagoWebpayViewModel
import com.cuidadoeterno.app.modules.servicio.data.model.OrdenResponse
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.SolicitudesActivasScreen
import com.cuidadoeterno.app.modules.servicio.ui.cliente.solicitud.SolicitudesActivasViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.disponibles.SolicitudesDisponiblesScreen
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.disponibles.SolicitudesDisponiblesViewModel
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.evaluacion.EvaluacionSolicitudScreen
import com.cuidadoeterno.app.modules.servicio.ui.cuidador.evaluacion.EvaluacionSolicitudViewModel

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

    val adminApiService      = retrofit.create(AdminApiService::class.java)
    val adminRepository      = AdminRepository(adminApiService)
    
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
                        HomeClienteViewModel(sessionManager, servicioRepository) as T
                }
            )

            HomeClienteScreen(
                viewModel = viewModel,
                onServiciosClick = { navController.navigate(NavRoutes.FLUJO_SOLICITUD) },
                onNosotrosClick = { navController.navigate("nosotros") },
                onFaqClick = { navController.navigate("faq") },
                onVerSolicitudesActivas = { navController.navigate(NavRoutes.SOLICITUDES_ACTIVAS) }, // <-- CAMBIO
                onVerHistorial   = { navController.navigate(NavRoutes.HISTORIAL_CLIENTE) },
                onVerPerfil      = { navController.navigate(NavRoutes.PERFIL) },
                onCerrarSesion   = {
                    navController.navigate(NavRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // =======================================================================
        // ── HOME CUIDADOR ──────────────────────────────────────────────────────
        // =======================================================================
        composable(NavRoutes.HOME_CUIDADOR) {
            val viewModel: HomeCuidadorViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        HomeCuidadorViewModel(sessionManager, authRepository) as T
                }
            )
            HomeCuidadorScreen(
                viewModel = viewModel,
                onVerSolicitudesDisponibles = { navController.navigate(NavRoutes.SOLICITUDES_DISPONIBLES) },
                onVerHistorial = { navController.navigate(NavRoutes.HISTORIAL_CUIDADOR) },
                onVerPagos = { /* Futura implementación */ },
                onVerPerfil = { navController.navigate(NavRoutes.PERFIL) },
                onCerrarSesion = {
                    navController.navigate(NavRoutes.LOGIN) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(NavRoutes.SOLICITUDES_DISPONIBLES) {
            val viewModel: SolicitudesDisponiblesViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        SolicitudesDisponiblesViewModel(servicioRepository) as T
                }
            )

            // Obtenemos el SavedStateHandle de la RUTA DESTINO (Evaluacion) para pasar la orden seleccionada.
            // Compose Navigation no pasa objetos complejos por URL, así que usamos currentBackStackEntry
            SolicitudesDisponiblesScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onSolicitudSeleccionada = { ordenCompleta ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("orden_seleccionada", ordenCompleta)
                    navController.navigate(NavRoutes.EVALUACION_SOLICITUD)
                }
            )
        }

    // 3. PANTALLA DE EVALUACIÓN (La del botón "ACEPTAR TRABAJO")
        composable(NavRoutes.EVALUACION_SOLICITUD) {
            // Recuperamos la orden desde el stateHandle que seteamos en el paso anterior
            val orden = navController.previousBackStackEntry?.savedStateHandle?.get<OrdenResponse>("orden_seleccionada")

            val viewModel: EvaluacionSolicitudViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        EvaluacionSolicitudViewModel(servicioRepository, sessionManager) as T
                }
            )

            // Le inyectamos la orden al viewmodel
            LaunchedEffect(orden) {
                orden?.let { viewModel.cargarDetalleOrden(it) }
            }

            EvaluacionSolicitudScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onIrAEjecucion = { idOrden ->
                    // Al aceptar, limpiamos el stack hasta el Home y navegamos al detalle activo
                    navController.navigate("detalle_orden/$idOrden") {
                        popUpTo(NavRoutes.HOME_CUIDADOR) { inclusive = false }
                    }
                }
            )
        }

// 4. PANTALLA DE DETALLE/TRABAJO (Donde se cambia "En Camino", "En Sitio", etc)
        composable(
            route = "detalle_orden/{idOrden}",
            arguments = listOf(navArgument("idOrden") { type = NavType.IntType })
        ) { backStackEntry ->
            val idOrden = backStackEntry.arguments?.getInt("idOrden") ?: 0

            val viewModel: DetalleOrdenCuidadorViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        DetalleOrdenCuidadorViewModel(servicioRepository, sessionManager) as T
                }
            )

            // Necesitamos cargar la orden desde el backend por su ID
            // (Asumo que tienes un método obtenerOrdenPorId en tu repository/API)
            LaunchedEffect(idOrden) {
                // viewModel.cargarOrden(idOrden) <-- Deberás agregar este método si no lo tienes
            }

            DetalleOrdenCuidadorScreen(
                viewModel = viewModel,
                onVolver = {
                    navController.navigate(NavRoutes.HOME_CUIDADOR) {
                        popUpTo(NavRoutes.HOME_CUIDADOR) { inclusive = true }
                    }
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
                        navController.navigate(NavRoutes.STEP_PREGUNTA_PRODUCTOS)
                    }
                )
            }
        }

        // INTERSTICIAL DE PREGUNTA
        composable(NavRoutes.STEP_PREGUNTA_PRODUCTOS) { backStackEntry ->
            // Recuperamos el FlowViewModel compartido
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavRoutes.FLUJO_SOLICITUD)
            }
            val flowViewModel: SolicitudFlowViewModel = viewModel(viewModelStoreOwner = parentEntry)

            PreguntaProductosScreen(
                onSi = {
                    navController.navigate(NavRoutes.STEP_CATALOGO)
                },
                onNo = {
                    // LIMPIEZA CLAVE: Si dice que no, aseguramos que el carrito quede vacío
                    flowViewModel.limpiarInsumos()
                    // Si dice que NO, vamos directo al detalle final del flujo
                    // (Asegúrate de que STEP_RESUMEN o similar sea tu pantalla de confirmación final)
                    navController.navigate(NavRoutes.STEP_RESUMEN)
                },
                onBack = { navController.popBackStack() }
            )
        }

        // PASO 3: CATÁLOGO DE PRODUCTOS
        composable(NavRoutes.STEP_CATALOGO) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavRoutes.FLUJO_SOLICITUD)
            }
            val flowViewModel: SolicitudFlowViewModel = viewModel(viewModelStoreOwner = parentEntry)

            // Instancias tu ApiClient y el ViewModel del catálogo como lo tenías
            val retrofit = ApiClient.createRetrofit(authInterceptor)
            val inventarioApi = retrofit.create(InventarioApiService::class.java)
            val catalogoViewModel: CatalogoProductoViewModel = viewModel(
                factory = CatalogoViewModelFactory(InventarioRepository(inventarioApi))
            )

            CatalogoProductoScreen(
                viewModel = catalogoViewModel,
                flowViewModel = flowViewModel,
                onBack = { navController.popBackStack() },
                onVerCarrito = {
                    // AHORA ESTE BOTÓN TE LLEVA DIRECTO AL RESUMEN FINAL
                    navController.navigate(NavRoutes.STEP_RESUMEN)
                }
            )
        }

        // PASO 4: RESUMEN FINAL Y PAGO (Ruta que unifica todo)
        composable(NavRoutes.STEP_RESUMEN) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                navController.getBackStackEntry(NavRoutes.FLUJO_SOLICITUD)
            }
            val flowViewModel: SolicitudFlowViewModel = viewModel(viewModelStoreOwner = parentEntry)

            val resumenViewModel: ResumenSolicitudViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        ResumenSolicitudViewModel(servicioRepository, finanzasRepository) as T
                }
            )

            ResumenSolicitudScreen(
                viewModel = resumenViewModel,
                flowViewModel = flowViewModel,
                onBack = { navController.popBackStack() },
                onNavegarAWebpay = { token, url ->
                    // Usamos tu helper de NavRoutes que ya hace el URLEncoder por dentro
                    navController.navigate(NavRoutes.pagoWebpay(url = url, token = token))
                }
            )
        }

        // 2. Pantalla con el WebView de Transbank Webpay
        composable(
            route = NavRoutes.PAGO_WEBPAY, // "pago_webpay/{url}/{token}"
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("token") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // Rescatamos los argumentos y decodificamos la URL
            val urlCodificada = backStackEntry.arguments?.getString("url") ?: ""
            val urlWebpay = java.net.URLDecoder.decode(urlCodificada, "UTF-8")
            val token = backStackEntry.arguments?.getString("token") ?: ""

            // ¡AQUÍ ESTÁ LA CORRECCIÓN! Usamos el Factory manual en lugar de hiltViewModel()
            val pagoViewModel: PagoWebpayViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        PagoWebpayViewModel(finanzasRepository) as T // <--- Pasa tu repositorio de finanzas aquí
                }
            )

            PagoWebpayScreen(
                urlWebpay = urlWebpay,
                token = token,
                viewModel = pagoViewModel,
                onIrAConfirmacion = { esExitoso, numOrden, codAuth ->
                    // Usamos tu helper para ir a la confirmación
                    navController.navigate(NavRoutes.confirmacionPago(esExitoso, numOrden, codAuth)) {
                        // Limpiamos todo el flujo de solicitud para que no pueda volver atrás al pago
                        popUpTo(NavRoutes.FLUJO_SOLICITUD) { inclusive = true }
                    }
                }
            )
        }

        // 3. Pantalla Final de Confirmación de Pago
        composable(
            route = NavRoutes.CONFIRMACION_PAGO,
            arguments = listOf(
                navArgument("esExitoso") { type = NavType.BoolType },
                navArgument("numOrden") { type = NavType.StringType },
                navArgument("codAuth") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val esExitoso = backStackEntry.arguments?.getBoolean("esExitoso") ?: false
            val numOrden = backStackEntry.arguments?.getString("numOrden") ?: ""
            val codAuth = backStackEntry.arguments?.getString("codAuth") ?: ""

            ConfirmacionPagoScreen(
                esExitoso = esExitoso,
                numeroOrden = numOrden,
                codigoAutorizacion = codAuth,
                onVerMisSolicitudes = {
                    navController.navigate(NavRoutes.HOME_CLIENTE) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    }
                }
            )
        }

        // =======================================================================
        // ── CLIENTE: SOLICITUDES ACTIVAS (redirige al detalle si hay 1) ────────
        // =======================================================================
        composable(NavRoutes.SOLICITUDES_ACTIVAS) {
            val viewModel: SolicitudesActivasViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        SolicitudesActivasViewModel(servicioRepository, sessionManager) as T
                }
            )

            SolicitudesActivasScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() },
                onIrADetalle = { idOrden ->
                    navController.navigate(NavRoutes.detalleSolicitud(idOrden)) {
                        // Sacamos SOLICITUDES_ACTIVAS del backstack para que
                        // "atrás" desde el detalle vuelva directo al Home
                        popUpTo(NavRoutes.SOLICITUDES_ACTIVAS) { inclusive = true }
                    }
                }
            )
        }

        // ── Historiales (Implementación Futura) ────────────────────────────────
        composable(NavRoutes.HISTORIAL_CLIENTE) { }
        composable(NavRoutes.HISTORIAL_CUIDADOR) { }

        // =======================================================================
        // ── PERFIL DE USUARIO ──────────────────────────────────────────────────
        // =======================================================================
        composable(NavRoutes.PERFIL) {
            val viewModel: PerfilViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T =
                        // Solo pasamos el authRepository, eliminamos el sessionManager
                        PerfilViewModel(authRepository) as T
                }
            )

            PerfilScreen(
                viewModel = viewModel,
                // Usamos el nombre exacto del parámetro que definiste: onLogout
                onLogout = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }


}