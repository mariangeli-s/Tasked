package com.example.tasked.ui.auth.composable

import android.widget.Toast
import androidx.compose.foundation.clickable
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.data.repository.UserRepository
import com.example.tasked.ui.auth.viewmodel.RegisterViewModel
import com.example.tasked.ui.auth.viewmodel.RegisterViewModelFactory
import com.example.tasked.ui.theme.TaskedTheme
import com.example.tasked.utils.Resource
import com.example.tasked.utils.SharedPreferencesManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.app.Activity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(UserRepository(RetrofitClient.apiService))
    )
) {
    val context = LocalContext.current
    //val activity = context as Activity
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("") }

    // Estado para controlar la visibilidad del DatePickerModal
    var showDatePicker by remember { mutableStateOf(false) }

    // Log para depurar el cambio de estado de showDatePicker
    LaunchedEffect(showDatePicker) {
        Log.d("RegisterScreen", "showDatePicker changed to: $showDatePicker (from LaunchedEffect)")
    }

    val registerResult by registerViewModel.registerResult.observeAsState()

    // Date Picker Dialog
    //val calendar = Calendar.getInstance()
    //val year = calendar.get(Calendar.YEAR)
    //val month = calendar.get(Calendar.MONTH)
    //val day = calendar.get(Calendar.DAY_OF_MONTH)


    // Manejar el resultado del registro
    LaunchedEffect(registerResult) {
        when (registerResult) {
            is Resource.Success<*> -> {
                val authResponse = (registerResult as Resource.Success).data
                authResponse?.let {
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
                        age = it.user.age, // int?
                        dateOfBirth = it.user.dateOfBirth // string?
                    )

                    Toast.makeText(
                        context,
                        "¡Registro exitoso! Bienvenido/a, ${it.user.username}!",
                        Toast.LENGTH_SHORT
                    ).show()
                    onRegisterSuccess()
                }
            }

            is Resource.Error<*> -> {
                val errorMessage = registerResult?.message ?: "Error desconocido"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }

            is Resource.Loading<*> -> {} // Estado de carga
            null -> {} // Estado inicial
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

            // Fila 1: Nombre de usuario y Contraseña
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Nombre de usuario") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            )

            // Fila 2: Nombre y Apellido
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Apellido") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Fila 3: Correo y Teléfono
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono (66660000)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Teléfono") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Fila 4: Dirección y Edad
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Dirección") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Dirección") },
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                            age = newValue
                        }
                    },
                    label = { Text("Edad") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Fila 5: Fecha de Nacimiento
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable {
                        Log.d("RegisterScreen", "Clic detectado en campo Fecha de Nacimiento.")
                        showDatePicker = true
                    }
            ) {
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { /* Read-only, handled by DatePickerModal */ },
                    label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Fecha de Nacimiento") },
                    readOnly = true, // Sigue siendo de solo lectura
                    enabled = false, // Deshabilita el campo para evitar interacción nativa del TextField
                    modifier = Modifier.fillMaxWidth() // Asegura que el TextField ocupe el ancho completo del Box
                )
            }

            Button(
                onClick = {
                    val parsedAge = age.toIntOrNull()
                    if (username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
                        registerViewModel.register(
                            username,
                            password,
                            firstName.ifEmpty { null },
                            lastName.ifEmpty { null },
                            email,
                            phone.ifEmpty { null },
                            address.ifEmpty { null },
                            parsedAge,
                            dateOfBirth.ifEmpty { null }
                        )
                    } else {
                        Toast.makeText(context, "Por favor, ingresa usuario, contraseña y correo (obligatorios)", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = registerResult !is Resource.Loading<*>
            ) {
                if (registerResult is Resource.Loading<*>) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Registrarse", fontSize = 18.sp)
                }
            }
        }
    }

    // El DatePickerModal se muestra condicionalmente
    if (showDatePicker) {
        DatePickerModal(
            onDateSelected = { millis ->
                if (millis != null) {
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    dateOfBirth = dateFormat.format(millis)
                }
                Log.d("RegisterScreen", "Date selected or dismissed. Setting showDatePicker = false")
                showDatePicker = false // Cerrar el DatePicker
            },
            onDismiss = {
                Log.d("RegisterScreen", "DatePicker dismissed. Setting showDatePicker = false")
                showDatePicker = false
            } // Cerrar el DatePicker al descartar
        )
    }
}

// El nuevo Composable DatePickerModal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
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