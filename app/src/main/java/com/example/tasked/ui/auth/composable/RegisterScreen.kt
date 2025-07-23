package com.example.tasked.ui.auth.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.tasked.ui.auth.viewmodel.RegisterViewModel
import com.example.tasked.ui.auth.viewmodel.RegisterViewModelFactory
import com.example.tasked.data.repository.UserRepository
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.ui.theme.TaskedTheme
import com.example.tasked.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(UserRepository(RetrofitClient.apiService))
    )
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val registerResult by registerViewModel.registerResult.observeAsState()

    // Manejar el resultado del registro
    LaunchedEffect(registerResult) {
        when (registerResult) {
            is Resource.Success -> {
                Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show()
                onRegisterSuccess()
            }
            is Resource.Error -> {
                val errorMessage = registerResult?.message ?: "Error desconocido"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            else -> {} // Loading o null
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
                text = "Registrarse",
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
                        registerViewModel.register(username, password)
                    } else {
                        Toast.makeText(context, "Por favor, ingresa usuario y contraseña", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = registerResult !is Resource.Loading
            ) {
                if (registerResult is Resource.Loading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrarse", fontSize = 18.sp)
                }
            }
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    TaskedTheme {
        RegisterScreen(
            onRegisterSuccess = {}
        )
    }
}