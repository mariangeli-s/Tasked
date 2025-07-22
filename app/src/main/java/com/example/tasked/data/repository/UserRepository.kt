package com.example.tasked.data.repository

import com.example.tasked.data.model.AuthResponse
import com.example.tasked.data.model.LoginRequest
import com.example.tasked.data.model.RegisterRequest
import com.example.tasked.data.remote.ApiService
import retrofit2.Response

class UserRepository(private val apiService: ApiService) {

    suspend fun login(request: LoginRequest): Response<AuthResponse> {
        return apiService.login(request)
    }

    suspend fun register(request: RegisterRequest): Response<AuthResponse> {
        return apiService.register(request)
    }
}
