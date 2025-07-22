package com.example.tasked.ui.auth.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasked.data.model.AuthResponse
import com.example.tasked.data.model.LoginRequest
import com.example.tasked.data.repository.UserRepository
import com.example.tasked.utils.Resource
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Resource<AuthResponse>>()
    val loginResult: LiveData<Resource<AuthResponse>> = _loginResult

    fun login(username: String, password: String) {
        _loginResult.value = Resource.Loading()
        viewModelScope.launch {
            try {
                val request = LoginRequest(username, password)
                val response = userRepository.login(request)
                if (response.isSuccessful && response.body() != null) {
                    _loginResult.value = Resource.Success(response.body()!!)
                } else {
                    _loginResult.value = Resource.Error(response.message() ?: "Error de inicio de sesión")
                }
            } catch (e: Exception) {
                _loginResult.value = Resource.Error("Error de red: ${e.localizedMessage}")
            }
        }
    }
}
