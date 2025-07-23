package com.example.tasked.data.remote

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.tasked.data.model.AuthResponse
import com.example.tasked.data.model.LoginRequest
import com.example.tasked.data.model.RegisterRequest
import com.example.tasked.data.model.Task
import com.example.tasked.data.model.CreateTaskRequest
import com.example.tasked.data.model.UpdateTaskStatusRequest
import com.example.tasked.data.model.User
import com.example.tasked.ui.auth.composable.LoginScreen
import com.example.tasked.ui.theme.TaskedTheme
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Path
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {

    @POST("auth/login") // Asegúrate de que esta sea la ruta correcta de tu API
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register") // Asegúrate de que esta sea la ruta correcta de tu API
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // Endpoints para tareas
    @POST("tasks")
    suspend fun createTask(
        @Header("Authorization") token: String,
        @Body request: CreateTaskRequest
    ): Response<Task>

    @GET("tasks/my") // Para obtener tareas personales o asignadas al usuario logueado
    suspend fun getMyTasks(
        @Header("Authorization") token: String
    ): Response<List<Task>>

    @GET("tasks/assigned-by-me") // Para jefes: tareas que ellos asignaron
    suspend fun getAssignedTasksByMe(
        @Header("Authorization") token: String
    ): Response<List<Task>>

    @GET("tasks/all") // Opcional: Para jefes que puedan ver todas las tareas (si tu backend lo permite)
    suspend fun getAllTasks(
        @Header("Authorization") token: String
    ): Response<List<Task>>

    @GET("users") // Para que los jefes puedan buscar usuarios a quienes asignar tareas
    suspend fun getUsers(
        @Header("Authorization") token: String,
        @Query("role") role: String = "employee" // Podrías filtrar por rol si tu API lo soporta
    ): Response<List<User>> // Reutilizamos la clase User para la lista de usuarios

    // Nuevo endpoint para actualizar el estado de una tarea
    @PATCH("tasks/{taskId}") // Ajusta la ruta si tu API usa otra convención
    suspend fun updateTaskStatus(
        @Header("Authorization") token: String,
        @Path("taskId") taskId: String,
        @Body request: UpdateTaskStatusRequest
    ): Response<Task> // Podría devolver la tarea actualizada o solo un éxito
}
