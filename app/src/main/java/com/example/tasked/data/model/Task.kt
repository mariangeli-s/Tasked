package com.example.tasked.data.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val createdBy: Int?, // ID del usuario que creó la tarea
    val assignedTo: Int?, // ID del usuario asignado (null si es personal)
    val creator: User?, // Nombre del usuario que creó la tarea
    val assignee: User?, // Nombre del usuario asignado (null si es personal)
    val status: String // Ej: "pending", "completed"
)

// Clases para requests de tareas
data class CreateTaskRequest(
    val title: String,
    val description: String,
    val assignedTo: Int? = null // Opcional, solo para jefes
)

// Nueva clase para la petición de actualización de estado
data class UpdateTaskStatusRequest(
    val status: String // Por ejemplo, "completed"
)

data class UpdateTaskRequest(
    val token: String,
    val title: String,
    val description: String,
    val assignedTo: Int? = null // Opcional, solo para jefes
)

data class AssignTaskRequest(
    val assignedTo: Int
)
