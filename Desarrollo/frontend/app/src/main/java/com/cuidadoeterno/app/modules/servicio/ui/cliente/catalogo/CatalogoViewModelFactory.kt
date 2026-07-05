package com.cuidadoeterno.app.modules.servicio.ui.cliente.catalogo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cuidadoeterno.app.modules.servicio.data.repository.InventarioRepository

class CatalogoViewModelFactory(private val repository: InventarioRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogoProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogoProductoViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel no reconocido")
    }
}