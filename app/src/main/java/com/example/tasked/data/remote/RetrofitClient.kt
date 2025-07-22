package com.example.tasked.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // Cambia esta URL por la URL base de tu API REST
    // Si estás probando en un emulador, "10.0.2.2" apunta a tu máquina local.
    // Si estás probando en un dispositivo físico, necesitas la IP de tu máquina en la red local.
    private const val BASE_URL = "http://10.0.2.2:5000/api/v1/" // Ejemplo: ajusta el puerto y la ruta

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Esto mostrará los detalles de la petición y respuesta en Logcat
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS) // Tiempo máximo para establecer la conexión
        .readTimeout(30, TimeUnit.SECONDS)    // Tiempo máximo para leer la respuesta
        .writeTimeout(30, TimeUnit.SECONDS)   // Tiempo máximo para enviar los datos
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
