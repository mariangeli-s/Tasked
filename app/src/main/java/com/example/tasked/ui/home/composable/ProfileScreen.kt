package com.example.tasked.ui.home.composable

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
//import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tasked.ui.theme.TaskedTheme
import com.example.tasked.utils.SharedPreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) }

    val username = sharedPreferencesManager.getUsername() ?: "N/A"
    val role = sharedPreferencesManager.getUserRole() ?: "N/A"
    val userId = sharedPreferencesManager.getUserId() ?: "N/A"
    val firstName = sharedPreferencesManager.getFirstName() ?: "N/A" // Nuevo
    val lastName = sharedPreferencesManager.getLastName() ?: "N/A" // Nuevo
    val email = sharedPreferencesManager.getEmail() ?: "N/A" // Nuevo
    val phone = sharedPreferencesManager.getPhone() ?: "N/A" // Nuevo
    val address = sharedPreferencesManager.getAddress() ?: "N/A" // Nuevo
    val age = sharedPreferencesManager.getAge() // Nuevo
    val dateOfBirth = sharedPreferencesManager.getDateOfBirth() ?: "N/A" // Nuevo

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Información del Usuario",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    ProfileInfoRow(label = "Nombre de usuario:", value = username)
                    ProfileInfoRow(label = "Nombre:", value = firstName) // Nuevo
                    ProfileInfoRow(label = "Apellido:", value = lastName) // Nuevo
                    ProfileInfoRow(label = "Correo:", value = email) // Nuevo
                    ProfileInfoRow(label = "Teléfono:", value = phone) // Nuevo
                    ProfileInfoRow(label = "Dirección:", value = address) // Nuevo
                    ProfileInfoRow(label = "Edad:", value = age?.toString() ?: "N/A") // Nuevo
                    ProfileInfoRow(label = "Fecha de Nacimiento:", value = dateOfBirth) // Nuevo
                    ProfileInfoRow(label = "Rol:", value = role.capitalize())
                    ProfileInfoRow(label = "ID de Usuario:", value = userId)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(0.6f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    TaskedTheme {
        ProfileScreen(
            onNavigateBack = {},
            onLogout = {}
        )
    }
}