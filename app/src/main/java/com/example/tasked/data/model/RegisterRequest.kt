package com.example.tasked.data.model

data class RegisterRequest(
    val username: String,
    val password: String,
    val firstName: String?, // Nuevo campo
    val lastName: String?, // Nuevo campo
    val email: String, // Nuevo campo, asumiendo que es obligatorio
    val phone: String?, // Nuevo campo
    val address: String?, // Nuevo campo
    val age: Int?, // Nuevo campo
    val dateOfBirth: String? // Nuevo campo (formato ISO 8601 como "YYYY-MM-DD")
)
