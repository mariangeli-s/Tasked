package com.example.tasked.ui.auth.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasked.data.repository.UserRepository
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.ui.auth.viewmodel.LoginViewModel
import com.example.tasked.ui.auth.viewmodel.LoginViewModelFactory
import com.example.tasked.ui.theme.TaskedTheme
import com.example.tasked.utils.Resource
import com.example.tasked.utils.SharedPreferencesManager
//import android.app.Activity
//import android.content.Intent
//import com.example.tasked.ui.home.MainActivity

//@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    loginViewModel: LoginViewModel = viewModel(
        factory = LoginViewModelFactory(UserRepository(RetrofitClient.apiService))
    )
) {
    val context = LocalContext.current
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) } // Inicializa SharedPreferencesManager

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginResult by loginViewModel.loginResult.observeAsState()

    // Manejar el resultado del login
    LaunchedEffect(loginResult) {
        val result = loginResult // Obtener el valor actual del Resource
        when (result) {
            is Resource.Success<*> -> {
                val authResponse = (result as Resource.Success).data
                authResponse?.let {
                    // Guardar todos los datos del usuario en SharedPreferences
                    sharedPreferencesManager.saveAuthData(
                        token = it.token,
                        userId = it.user.id,
                        username = it.user.username,
                        userRole = it.user.role,
                        firstName = it.user.firstName,
                        lastName = it.user.lastName,
                        email = it.user.email,
                        phone = it.user.phone,
                        address = it.user.address,
                        age = it.user.age,
                        dateOfBirth = it.user.dateOfBirth
                    )

                    Toast.makeText(context, "¡Bienvenido/a, ${it.user.username}!", Toast.LENGTH_SHORT).show()
                    onLoginSuccess()
                }
            }
            is Resource.Error<*> -> {
                val errorMessage = result.message ?: "Error desconocido"
                Toast.makeText(context, "Error de login: $errorMessage", Toast.LENGTH_LONG).show()
            }
            is Resource.Loading<*> -> {
                // Estado de carga, no hacemos nada aquí visiblemente, el botón ya está deshabilitado
            }
            null -> {
                // Estado inicial, no hacemos nada
            }
        }
    }
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
                text = "Iniciar Sesión",
                fontSize = 28.sp,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            Button(
                onClick = {
                    if (username.isNotEmpty() && password.isNotEmpty()) {
                        loginViewModel.login(username, password)
                    } else {
                        Toast.makeText(context, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = loginResult !is Resource.Loading
            ) {
                if (loginResult is Resource.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Iniciar Sesión", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onNavigateToRegister) {
                Text("¿No tienes cuenta? Regístrate aquí", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    TaskedTheme {
        LoginScreen(
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}

