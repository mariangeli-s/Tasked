package com.example.tasked.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasked.ui.home.MainActivity
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.data.repository.UserRepository
import com.example.tasked.ui.auth.composable.LoginScreen
import com.example.tasked.ui.auth.viewmodel.LoginViewModel
import com.example.tasked.ui.auth.viewmodel.LoginViewModelFactory
import com.example.tasked.ui.theme.TaskedTheme // Asegúrate de que tu tema esté definido aquí
import com.example.tasked.utils.SharedPreferencesManager

class LoginActivity : ComponentActivity() {

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferencesManager = SharedPreferencesManager(this)

        // Si el usuario ya está logueado, ir a la pantalla principal
        if (sharedPreferencesManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        setContent {
            TaskedTheme {
                val loginViewModel: LoginViewModel = viewModel(
                    factory = LoginViewModelFactory(UserRepository(RetrofitClient.apiService))
                )
                LoginScreen(
                    onLoginSuccess = { navigateToMain() },
                    onNavigateToRegister = { navigateToRegister() },
                    loginViewModel = loginViewModel
                )
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }
}