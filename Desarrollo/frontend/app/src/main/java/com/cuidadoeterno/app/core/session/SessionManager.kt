package com.cuidadoeterno.app.core.session

//Guarda JWT y datos del usuario en DataStore
import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {

    companion object {
        private val JWT_TOKEN_KEY   = stringPreferencesKey("jwt_token")
        private val ROL_KEY         = stringPreferencesKey("rol")
        private val ID_PERSONA_KEY  = intPreferencesKey("id_persona")
        private val NOMBRE_KEY      = stringPreferencesKey("nombre")
    }

    suspend fun saveSession(token: String, rol: String, idPersona: Int, nombre: String) {
        context.dataStore.edit { prefs ->
            prefs[JWT_TOKEN_KEY]  = token
            prefs[ROL_KEY]        = rol
            prefs[ID_PERSONA_KEY] = idPersona
            prefs[NOMBRE_KEY]     = nombre
        }
    }

    val authToken: Flow<String?> = context.dataStore.data.map { it[JWT_TOKEN_KEY] }
    val rol: Flow<String?>       = context.dataStore.data.map { it[ROL_KEY] }
    val idPersona: Flow<Int?>    = context.dataStore.data.map { it[ID_PERSONA_KEY] }
    val nombre: Flow<String?>    = context.dataStore.data.map { it[NOMBRE_KEY] }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}