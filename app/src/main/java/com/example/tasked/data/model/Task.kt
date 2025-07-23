package com.example.tasked.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val createdBy: String, // ID del usuario que creó la tarea
    val assignedTo: String?, // ID del usuario asignado (null si es personal)
    val status: String // Ej: "pending", "completed"
)

// Clases para requests de tareas
data class CreateTaskRequest(
    val title: String,
    val description: String,
    val assignedTo: String? = null // Opcional, solo para jefes
)

// Nueva clase para la petición de actualización de estado
data class UpdateTaskStatusRequest(
    val status: String // Por ejemplo, "completed"
)
