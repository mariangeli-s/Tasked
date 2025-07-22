package com.example.tasked.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasked.ui.auth.LoginActivity
import com.example.tasked.ui.theme.TaskedTheme
import com.example.tasked.utils.SharedPreferencesManager

class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferencesManager = SharedPreferencesManager(this)

        // Verificar si el usuario está logueado
        if (!sharedPreferencesManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        setContent {
            TaskedTheme {
                MainScreen(
                    username = sharedPreferencesManager.getUsername() ?: "Usuario",
                    role = sharedPreferencesManager.getUserRole() ?: "Desconocido",
                    onLogout = {
                        sharedPreferencesManager.clearAuthData()
                        navigateToLogin()
                    }
                )
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun MainScreen(username: String, role: String, onLogout: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido, $username!",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Tu rol: $role",
                fontSize = 18.sp,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Button(
                onClick = onLogout,
                modifier = Modifier
                    .wrapContentWidth()
                    .height(50.dp)
            ) {
                Text("Cerrar Sesión", fontSize = 18.sp)
            }
            // Aquí irían los botones o composables para la gestión de tareas
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskedTheme {
        //MainScreen()
    }
}