package com.example.tasked.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.data.repository.UserRepository
import com.example.tasked.ui.auth.composable.RegisterScreen
import com.example.tasked.ui.auth.viewmodel.RegisterViewModel
import com.example.tasked.ui.auth.viewmodel.RegisterViewModelFactory
import com.example.tasked.ui.home.MainActivity
import com.example.tasked.ui.theme.TaskedTheme // Asegúrate de que tu tema esté definido aquí
import com.example.tasked.utils.SharedPreferencesManager

class RegisterActivity : ComponentActivity() {

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferencesManager = SharedPreferencesManager(this)

        setContent {
            TaskedTheme {
                val registerViewModel: RegisterViewModel = viewModel(
                    factory = RegisterViewModelFactory(UserRepository(RetrofitClient.apiService))
                )
                RegisterScreen(
                    onRegisterSuccess = { navigateToMain() },
                    registerViewModel = registerViewModel
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
}