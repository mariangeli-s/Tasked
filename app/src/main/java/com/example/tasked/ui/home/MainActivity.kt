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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasked.ui.auth.LoginActivity
import com.example.tasked.ui.home.composable.BossDashboardScreen
import com.example.tasked.ui.home.composable.EmployeeDashboardScreen
import com.example.tasked.ui.home.composable.ProfileScreen // Importa la pantalla de perfil
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
                val username = sharedPreferencesManager.getUsername() ?: "Usuario"
                val role = sharedPreferencesManager.getUserRole() ?: "Desconocido"

                // Estado para controlar qué pantalla se muestra (dashboard o perfil)
                var currentScreen by remember { mutableStateOf<MainAppScreen>(MainAppScreen.Dashboard) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        MainAppScreen.Dashboard -> {
                            when (role) {
                                "boss" -> BossDashboardScreen(
                                    username = username,
                                    onLogout = {
                                        sharedPreferencesManager.clearAuthData()
                                        navigateToLogin()
                                    },
                                    onNavigateToProfile = { currentScreen = MainAppScreen.Profile } // Navegar a perfil
                                )
                                "employee" -> EmployeeDashboardScreen(
                                    username = username,
                                    onLogout = {
                                        sharedPreferencesManager.clearAuthData()
                                        navigateToLogin()
                                    },
                                    onNavigateToProfile = { currentScreen = MainAppScreen.Profile } // Navegar a perfil
                                )
                                else -> {
                                    Column(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text("Rol desconocido: $role", fontSize = 20.sp)
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Button(onClick = {
                                            sharedPreferencesManager.clearAuthData()
                                            navigateToLogin()
                                        }) {
                                            Text("Cerrar Sesión")
                                        }
                                    }
                                }
                            }
                        }
                        MainAppScreen.Profile -> {
                            ProfileScreen(
                                onNavigateBack = { currentScreen = MainAppScreen.Dashboard }, // Volver al dashboard
                                onLogout = {
                                    sharedPreferencesManager.clearAuthData()
                                    navigateToLogin()
                                }
                            )
                        }
                    }
                }
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

// Clase sellada para representar las pantallas principales de la app
sealed class MainAppScreen {
    object Dashboard : MainAppScreen()
    object Profile : MainAppScreen()
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TaskedTheme {
    }
}