package com.example.tasked.data.remote

import com.example.tasked.data.model.AuthResponse
import com.example.tasked.data.model.LoginRequest
import com.example.tasked.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("auth/login") // Asegúrate de que esta sea la ruta correcta de tu API
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register") // Asegúrate de que esta sea la ruta correcta de tu API
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // Aquí irían los endpoints para tareas (crear, ver, asignar)
    // @GET("tasks")
    // suspend fun getTasks(@Header("Authorization") token: String): Response<List<Task>>
    // ...
}