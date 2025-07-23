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
        private const val KEY_FIRST_NAME = "firstName" // Nuevo
        private const val KEY_LAST_NAME = "lastName" // Nuevo
        private const val KEY_EMAIL = "email" // Nuevo
        private const val KEY_PHONE = "phone" // Nuevo
        private const val KEY_ADDRESS = "address" // Nuevo
        private const val KEY_AGE = "age" // Nuevo
        private const val KEY_DATE_OF_BIRTH = "dateOfBirth" // Nuevo
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserRole(role: String) {
        prefs.edit().putString(KEY_USER_ROLE, role).apply()
    }

    fun getUserRole(): String? {
        return prefs.getString(KEY_USER_ROLE, null)
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString(KEY_USER_ID, userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun saveUsername(username: String) {
        prefs.edit().putString(KEY_USERNAME, username).apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    // Nuevos métodos para guardar información adicional del usuario
    fun saveFirstName(firstName: String?) {
        prefs.edit().putString(KEY_FIRST_NAME, firstName).apply()
    }

    fun getFirstName(): String? {
        return prefs.getString(KEY_FIRST_NAME, null)
    }

    fun saveLastName(lastName: String?) {
        prefs.edit().putString(KEY_LAST_NAME, lastName).apply()
    }

    fun getLastName(): String? {
        return prefs.getString(KEY_LAST_NAME, null)
    }

    fun saveEmail(email: String) {
        prefs.edit().putString(KEY_EMAIL, email).apply()
    }

    fun getEmail(): String? {
        return prefs.getString(KEY_EMAIL, null)
    }

    fun savePhone(phone: String?) {
        prefs.edit().putString(KEY_PHONE, phone).apply()
    }

    fun getPhone(): String? {
        return prefs.getString(KEY_PHONE, null)
    }

    fun saveAddress(address: String?) {
        prefs.edit().putString(KEY_ADDRESS, address).apply()
    }

    fun getAddress(): String? {
        return prefs.getString(KEY_ADDRESS, null)
    }

    fun saveAge(age: Int?) {
        if (age != null) {
            prefs.edit().putInt(KEY_AGE, age).apply()
        } else {
            prefs.edit().remove(KEY_AGE).apply()
        }
    }

    fun getAge(): Int? {
        val age = prefs.getInt(KEY_AGE, -1) // -1 como valor por defecto si no existe
        return if (age != -1) age else null
    }

    fun saveDateOfBirth(dateOfBirth: String?) {
        prefs.edit().putString(KEY_DATE_OF_BIRTH, dateOfBirth).apply()
    }

    fun getDateOfBirth(): String? {
        return prefs.getString(KEY_DATE_OF_BIRTH, null)
    }

    fun clearAuthData() {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_ROLE)
            .remove(KEY_USER_ID)
            .remove(KEY_USERNAME)
            .remove(KEY_FIRST_NAME) // Nuevo
            .remove(KEY_LAST_NAME) // Nuevo
            .remove(KEY_EMAIL) // Nuevo
            .remove(KEY_PHONE) // Nuevo
            .remove(KEY_ADDRESS) // Nuevo
            .remove(KEY_AGE) // Nuevo
            .remove(KEY_DATE_OF_BIRTH) // Nuevo
            .apply()
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null && getUserRole() != null
    }
}
