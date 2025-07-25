package com.example.tasked.data.repository

import com.example.tasked.data.model.*
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
    //actualizar el estado de una tarea
    suspend fun updateTaskStatus(token: String, taskId: String, status: String): Response<Task> {
        val request = UpdateTaskStatusRequest(status)
        return apiService.updateTaskStatus("Bearer $token", taskId, request)
    }

    suspend fun assignTask(token: String, taskId: String, assignedTo: Int): Response<Task> {
        val request = AssignTaskRequest(assignedTo)
        return apiService.assignTask("Bearer $token", taskId, request)
    }

    suspend fun deleteTask(token: String, taskId: String): Response<Unit> {
        return apiService.deleteTask("Bearer $token", taskId)
    }

    suspend fun updateTask(token: String, taskId: String, title: String, description: String, assignedTo: Int): Response<Task>{
        val request = UpdateTaskRequest(token, title, description, assignedTo)
        return apiService.updateTask("Bearer $token", taskId, request)
    }


}