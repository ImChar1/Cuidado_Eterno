package com.cuidadoeterno.app.core.network

//Instancia Retrofit singleton
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    // El context-path /api/v1 viene de application.properties:
    // server.servlet.context-path=/api/v1
    // Sin esto, todos los endpoints van a responder 404.
    //
    // 10.0.2.2 es la IP que el emulador de Android usa para
    // apuntar al localhost de tu máquina donde corre Docker.
    // En dispositivo físico: usar la IP de tu máquina en la red local.
    private const val BASE_URL = "http://13.220.146.100/api/v1/"

    fun createRetrofit(authInterceptor: AuthInterceptor): Retrofit {
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)  // útil para subida de fotos
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}