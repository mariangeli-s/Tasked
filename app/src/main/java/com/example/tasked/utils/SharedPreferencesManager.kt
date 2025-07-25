package com.example.tasked.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "TaskedPrefs"
        private const val KEY_AUTH_TOKEN = "authToken"
        private const val KEY_USER_ROLE = "userRole"
        private const val KEY_USER_ID = "userId"
        private const val KEY_USERNAME = "username"
        private const val KEY_FIRST_NAME = "firstName"
        private const val KEY_LAST_NAME = "lastName"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phone"
        private const val KEY_ADDRESS = "address"
        private const val KEY_AGE = "age"
        private const val KEY_DATE_OF_BIRTH = "dateOfBirth"
    }


    fun saveAuthData(
        token: String,
        userId: Int,
        username: String,
        userRole: String,
        firstName: String? = null,
        lastName: String? = null,
        email: String? = null, // Email no puede ser null en el registro, pero para el guardar lo hacemos opcional
        phone: String? = null,
        address: String? = null,
        age: Int? = null,
        dateOfBirth: String? = null
    ) {
        prefs.edit().apply {
            putString(KEY_AUTH_TOKEN, token)
            putString(KEY_USER_ID, userId.toString())
            putString(KEY_USERNAME, username)
            putString(KEY_USER_ROLE, userRole)
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME, lastName)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putString(KEY_ADDRESS, address)
            // Para Int?, necesitamos un manejo específico para guardar null
            if (age != null) putInt(KEY_AGE, age) else remove(KEY_AGE)
            putString(KEY_DATE_OF_BIRTH, dateOfBirth)
            apply()
        }
    }


    // Métodos individuales para obtener datos (estos están bien)
    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun getUserId(): Int? {
        val userIdString = prefs.getString(KEY_USER_ID, null)
        return userIdString?.toIntOrNull() // Return the Int?
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun getFirstName(): String? {
        return prefs.getString(KEY_FIRST_NAME, null)
    }

    fun getLastName(): String? {
        return prefs.getString(KEY_LAST_NAME, null)
    }

    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun getPhone(): String? {
        return prefs.getString(KEY_PHONE, null)
    }

    fun getAddress(): String? {
        return prefs.getString(KEY_ADDRESS, null)
    }

    fun getAge(): Int? {
        val age = prefs.getInt(KEY_AGE, -1) // -1 como valor por defecto si no existe
        return if (age != -1) age else null
    }

    fun getDateOfBirth(): String? {
        return prefs.getString(KEY_DATE_OF_BIRTH, null)
    }


    // ¡FUNCIÓN CORREGIDA! Verifica todos los campos esenciales para que la sesión sea válida
    fun isLoggedIn(): Boolean {
        return getAuthToken() != null &&
                getUserId() != null &&
                getUsername() != null &&
                getUserRole() != null // Agregué estas verificaciones.
    }

    // Función para limpiar todos los datos de autenticación y perfil
    fun clearAuthData() {
        prefs.edit().clear().apply() // .clear() es más eficiente que remover uno por uno
    }
}
