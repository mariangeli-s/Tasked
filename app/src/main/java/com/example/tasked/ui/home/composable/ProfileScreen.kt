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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
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
    val age = sharedPreferencesManager.getAge()?.toString() ?: "N/A" // Nuevo
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
                modifier = Modifier.fillMaxWidth(0.9f), // Un poco más ancho para las dos columnas
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    // Fila 1: Nombre de usuario y Rol
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Usuario:",
                            value = username,
                            modifier = Modifier.weight(1f)
                        )
                        InfoColumn(
                            label = "Rol:",
                            value = role.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila 2: Nombre y Apellido
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Nombre:",
                            value = firstName,
                            modifier = Modifier.weight(1f)
                        )
                        InfoColumn(
                            label = "Apellido:",
                            value = lastName,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila 3: Correo y Teléfono
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Correo:",
                            value = email,
                            modifier = Modifier.weight(1f)
                        )
                        InfoColumn(
                            label = "Teléfono:",
                            value = phone,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila 4: Dirección y Edad
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        InfoColumn(
                            label = "Dirección:",
                            value = address,
                            modifier = Modifier.weight(1f)
                        )
                        InfoColumn(
                            label = "Edad:",
                            value = age,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // Fila 5: Fecha de Nacimiento
                    InfoColumn(
                        label = "Fecha de Nacimiento:",
                        value = dateOfBirth,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // ID de Usuario
                    //InfoColumn(
                    //label = "ID de Usuario:",
                    //value = userId,
                    //modifier = Modifier.fillMaxWidth()
                    //)
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
fun InfoColumn(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 2.dp)
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