    package com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo

    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.cuidadoeterno.app.core.network.NetworkResult
    import com.cuidadoeterno.app.modules.servicio.data.model.ProductoCatalogoResponse
    import com.cuidadoeterno.app.modules.servicio.data.repository.InventarioRepository
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow
    import kotlinx.coroutines.launch

    data class CatalogoUiState(
        val isLoading: Boolean = true,
        val error: String? = null,
        val productosDisponibles: List<ProductoCatalogoResponse> = emptyList()
    )

    class CatalogoProductoViewModel(
        private val inventarioRepository: InventarioRepository
    ) : ViewModel() {

        private val _uiState = MutableStateFlow(CatalogoUiState())
        val uiState: StateFlow<CatalogoUiState> = _uiState.asStateFlow()

        fun cargarCatalogo(idCementerio: Int) {
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                when (val result = inventarioRepository.obtenerCatalogoCompleto(idCementerio)) {
                    is NetworkResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            productosDisponibles = result.data ?: emptyList()
                        )
                    }
                    is NetworkResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    is NetworkResult.Loading -> Unit
                }
            }
        }
    }