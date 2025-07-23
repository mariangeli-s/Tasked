package com.example.tasked.ui.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.tasked.data.model.CreateTaskRequest
import com.example.tasked.data.model.Task
import com.example.tasked.data.model.User
import com.example.tasked.data.repository.TaskRepository
import com.example.tasked.utils.Resource
import kotlinx.coroutines.launch

class TaskViewModel(private val taskRepository: TaskRepository) : ViewModel() {

    private val _myTasks = MutableLiveData<Resource<List<Task>>>()
    val myTasks: LiveData<Resource<List<Task>>> = _myTasks

    private val _assignedTasksByMe = MutableLiveData<Resource<List<Task>>>()
    val assignedTasksByMe: LiveData<Resource<List<Task>>> = _assignedTasksByMe

    private val _createTaskResult = MutableLiveData<Resource<Task>>()
    val createTaskResult: LiveData<Resource<Task>> = _createTaskResult

    private val _updateTaskStatusResult = MutableLiveData<Resource<Task>>()
    val updateTaskStatusResult: LiveData<Resource<Task>> = _updateTaskStatusResult

    private val _users = MutableLiveData<Resource<List<User>>>()
    val users: LiveData<Resource<List<User>>> = _users

    fun fetchMyTasks(token: String) {
        _myTasks.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = taskRepository.getMyTasks(token)
                if (response.isSuccessful && response.body() != null) {
                    _myTasks.value = Resource.Success(response.body()!!)
                } else {
                    _myTasks.value = Resource.Error(response.message() ?: "Error al cargar mis tareas")
                }
            } catch (e: Exception) {
                _myTasks.value = Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun fetchAssignedTasksByMe(token: String) {
        _assignedTasksByMe.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = taskRepository.getAssignedTasksByMe(token)
                if (response.isSuccessful && response.body() != null) {
                    _assignedTasksByMe.value = Resource.Success(response.body()!!)
                } else {
                    _assignedTasksByMe.value = Resource.Error(response.message() ?: "Error al cargar tareas asignadas por mí")
                }
            } catch (e: Exception) {
                _assignedTasksByMe.value = Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun createTask(token: String, title: String, description: String, assignedTo: String?) {
        _createTaskResult.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val request = CreateTaskRequest(title, description, assignedTo)
                val response = taskRepository.createTask(token, request)
                if (response.isSuccessful && response.body() != null) {
                    _createTaskResult.value = Resource.Success(response.body()!!)
                    // Refrescar la lista de tareas relevante después de crear una
                    fetchMyTasks(token) // Siempre refrescar mis tareas
                    if (assignedTo != null) { // Si se asignó, refrescar las asignadas por mí
                        fetchAssignedTasksByMe(token)
                    }
                } else {
                    _createTaskResult.value = Resource.Error(response.message() ?: "Error al crear tarea")
                }
            } catch (e: Exception) {
                _createTaskResult.value = Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun markTaskAsCompleted(token: String, taskId: String) {
        _updateTaskStatusResult.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = taskRepository.updateTaskStatus(token, taskId, "completed")
                if (response.isSuccessful && response.body() != null) {
                    _updateTaskStatusResult.value = Resource.Success(response.body()!!)
                    // Refrescar ambas listas después de una actualización para que el cambio se vea
                    fetchMyTasks(token)
                    fetchAssignedTasksByMe(token)
                } else {
                    _updateTaskStatusResult.value = Resource.Error(response.message() ?: "Error al marcar tarea como completada")
                }
            } catch (e: Exception) {
                _updateTaskStatusResult.value = Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }

    fun fetchUsers(token: String) {
        _users.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val response = taskRepository.getUsers(token)
                if (response.isSuccessful && response.body() != null) {
                    _users.value = Resource.Success(response.body()!!)
                } else {
                    _users.value = Resource.Error(response.message() ?: "Error al cargar usuarios")
                }
            } catch (e: Exception) {
                _users.value = Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }
}

// Factory para TaskViewModel
class TaskViewModelFactory(private val taskRepository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}