package com.example.tasked.data.repository

import com.example.tasked.data.model.CreateTaskRequest
import com.example.tasked.data.model.Task
import com.example.tasked.data.model.User
import com.example.tasked.data.remote.ApiService
import retrofit2.Response

class TaskRepository(private val apiService: ApiService) {

    suspend fun createTask(token: String, request: CreateTaskRequest): Response<Task> {
        return apiService.createTask("Bearer $token", request)
    }

    suspend fun getMyTasks(token: String): Response<List<Task>> {
        return apiService.getMyTasks("Bearer $token")
    }

    suspend fun getAssignedTasksByMe(token: String): Response<List<Task>> {
        return apiService.getAssignedTasksByMe("Bearer $token")
    }

    suspend fun getAllTasks(token: String): Response<List<Task>> {
        return apiService.getAllTasks("Bearer $token")
    }

    suspend fun getUsers(token: String): Response<List<User>> {
        return apiService.getUsers("Bearer $token")
    }
}