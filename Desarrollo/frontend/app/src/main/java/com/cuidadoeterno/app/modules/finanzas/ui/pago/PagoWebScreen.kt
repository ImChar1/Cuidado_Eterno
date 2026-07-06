package com.cuidadoeterno.app.modules.finanzas.ui.pago
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PagoWebpayScreen(
    urlWebpay: String,
    token: String,
    viewModel: PagoWebpayViewModel,
    onIrAConfirmacion: (esExitoso: Boolean, numOrden: String, codAuth: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Estado local para saber si el WebView interno sigue descargando el formulario de Transbank
    var webViewCargandoPagina by remember { mutableStateOf(true) }

    // Observador para navegar cuando termine el flujo en el backend
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is PagoWebpayUiState.Exitoso -> {
                onIrAConfirmacion(true, "ORD-${state.data.codigoAutorizacion}", state.data.codigoAutorizacion)
            }
            is PagoWebpayUiState.Fallido -> {
                onIrAConfirmacion(false, "", "")
            }
            else -> Unit
        }
    }

    // Dibujamos la UI dependiendo del estado global
    when (uiState) {
        is PagoWebpayUiState.CargandoWebView -> {
            // Box obliga a que los elementos internos respeten el tamaño completo (fillMaxSize)
            Box(modifier = Modifier.fillMaxSize()) {

                AndroidView(
                    modifier = Modifier.fillMaxSize(), // CRÍTICO: Obliga al WebView a medir el 100% de la pantalla
                    factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true // Requerido por Transbank para guardar estados de sesión
                                loadsImagesAutomatically = true
                                useWideViewPort = true
                                loadWithOverviewMode = true
                                databaseEnabled = true
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    webViewCargandoPagina = true
                                }

                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    // Cuando Transbank dibuje la interfaz del banco, apagamos el loader local
                                    webViewCargandoPagina = false
                                }

                                override fun shouldOverrideUrlLoading(
                                    view: WebView?,
                                    request: WebResourceRequest?
                                ): Boolean {
                                    val urlStr = request?.url?.toString() ?: ""

                                    // Si Transbank intenta retornar a nuestro endpoint de confirmación
                                    if (urlStr.contains("/finanzas/webpay/confirmar")) {
                                        val tokenDevuelto = request?.url?.getQueryParameter("token_ws")

                                        if (!tokenDevuelto.isNullOrBlank()) {
                                            viewModel.confirmarPago(tokenDevuelto)
                                        } else {
                                            viewModel.confirmarPago("") // Usuario canceló en Webpay
                                        }
                                        return true // Interceptamos y abortamos la navegación web ordinaria
                                    }
                                    return false
                                }
                            }
                        }
                    },
                    update = { webView ->
                        // El bloque 'update' se ejecuta de forma segura garantizando que la vista ya existe en pantalla.
                        // Usamos un tag de control para evitar que re-inyecte el POST si el composable se recompone.
                        if (webView.url == null && webView.tag == null) {
                            webView.tag = "POST_ENVIADO"
                            val postData = "token_ws=$token".toByteArray(Charsets.UTF_8)
                            webView.postUrl(urlWebpay, postData)
                        }
                    }
                )

                // Si el WebView está cargando en segundo plano, mostramos un spinner encima para que no se vea blanco
                if (webViewCargandoPagina) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Conectando con Transbank Webpay...")
                    }
                }
            }
        }

        is PagoWebpayUiState.Confirmando -> {
            // Pantalla de espera mientras tu backend Spring Boot hace el commit de la transacción
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Validando tu pago seguro con Transbank...")
            }
        }
        else -> { /* El LaunchedEffect se encarga de cambiar de pantalla */ }
    }
}