package com.cuidadoeterno.app.modules.usuario.data.repository

import com.cuidadoeterno.app.core.network.NetworkResult
import com.cuidadoeterno.app.core.session.SessionManager
import com.cuidadoeterno.app.modules.usuario.data.model.*
import com.cuidadoeterno.app.modules.usuario.data.remote.AuthApiService

class AuthRepository(
    private val api: AuthApiService,
    private val sessionManager: SessionManager
) {

    suspend fun login(request: LoginRequest): NetworkResult<LoginResponse> {
        return try {
            val response = api.login(request)
            if (response.isSuccessful && response.body()?.data != null) {
                val data = response.body()!!.data!!
                // Guardar sesión automáticamente después del login exitoso
                sessionManager.saveSession(
                    token     = data.token,
                    rol       = data.rol,
                    idPersona = data.idPersona,
                    nombre    = data.nombre
                )
                NetworkResult.Success(data)
            } else {
                NetworkResult.Error(response.body()?.message ?: "Error al iniciar sesión")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun registrarCliente(request: RegistroClienteRequest): NetworkResult<Unit> {
        return try {
            val response = api.registrarCliente(request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(response.body()?.message ?: "Error al registrarse")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun registrarCuidador(request: RegistroCuidadorRequest): NetworkResult<Unit> {
        return try {
            val response = api.registrarCuidador(request)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(response.body()?.message ?: "Error al registrarse")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun obtenerPerfil(): NetworkResult<PerfilResponse> {
        return try {
            val response = api.obtenerPerfil()
            if (response.isSuccessful && response.body()?.data != null) {
                NetworkResult.Success(response.body()!!.data!!)
            } else {
                NetworkResult.Error("No se pudo obtener el perfil")
            }
        } catch (e: Exception) {
            NetworkResult.Error("Sin conexión: ${e.message}")
        }
    }

    suspend fun logout() {
        try { api.logout() } catch (_: Exception) {}
        sessionManager.clearSession()
    }
}