package com.example.tasked.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasked.data.model.AuthResponse
import com.example.tasked.data.model.RegisterRequest
import com.example.tasked.data.repository.UserRepository
import com.example.tasked.utils.Resource
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<Resource<AuthResponse>>()
    val registerResult: LiveData<Resource<AuthResponse>> = _registerResult

    fun register(username: String, password: String) {
        _registerResult.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val request = RegisterRequest(username, password)
                val response = userRepository.register(request)
                if (response.isSuccessful && response.body() != null) {
                    _registerResult.value = Resource.Success(response.body()!!)
                } else {
                    _registerResult.value = Resource.Error(response.message() ?: "Error de registro")
                }
            } catch (e: Exception) {
                _registerResult.value = Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }
}