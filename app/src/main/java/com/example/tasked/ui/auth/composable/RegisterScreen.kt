package com.example.tasked.ui.auth.composable

import android.app.DatePickerDialog
import android.widget.DatePicker
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
//import androidx.compose.material.icons.filled.
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    registerViewModel: RegisterViewModel = viewModel(
        factory = RegisterViewModelFactory(UserRepository(RetrofitClient.apiService))
    )
) {
    val context = LocalContext.current
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) } // Para guardar datos después del registro

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") } // Nuevo
    var lastName by remember { mutableStateOf("") } // Nuevo
    var email by remember { mutableStateOf("") } // Nuevo
    var phone by remember { mutableStateOf("") } // Nuevo
    var address by remember { mutableStateOf("") } // Nuevo
    var age by remember { mutableStateOf("") } // Nuevo (como String para TextField)
    var dateOfBirth by remember { mutableStateOf("") } // Nuevo

    val registerResult by registerViewModel.registerResult.observeAsState()

    // Date Picker Dialog
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, selectedYear: Int, selectedMonth: Int, selectedDayOfMonth: Int ->
            val selectedDate = Calendar.getInstance().apply {
                set(selectedYear, selectedMonth, selectedDayOfMonth)
            }
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateOfBirth = dateFormat.format(selectedDate.time)
        }, year, month, day
    )

    // Manejar el resultado del registro
    LaunchedEffect(registerResult) {
        when (registerResult) {
            is Resource.Success<*> -> {
                val authResponse = (registerResult as Resource.Success).data
                authResponse?.let {
                    // Guardar todos los datos del usuario en SharedPreferences
                    sharedPreferencesManager.saveAuthToken(it.token)
                    sharedPreferencesManager.saveUserRole(it.user.role)
                    sharedPreferencesManager.saveUserId(it.user.id)
                    sharedPreferencesManager.saveUsername(it.user.username)
                    sharedPreferencesManager.saveFirstName(it.user.firstName) // Guardar nuevo campo
                    sharedPreferencesManager.saveLastName(it.user.lastName) // Guardar nuevo campo
                    sharedPreferencesManager.saveEmail(it.user.email) // Guardar nuevo campo
                    sharedPreferencesManager.savePhone(it.user.phone) // Guardar nuevo campo
                    sharedPreferencesManager.saveAddress(it.user.address) // Guardar nuevo campo
                    sharedPreferencesManager.saveAge(it.user.age) // Guardar nuevo campo
                    sharedPreferencesManager.saveDateOfBirth(it.user.dateOfBirth) // Guardar nuevo campo

                    Toast.makeText(context, "¡Registro exitoso! Bienvenido, ${it.user.username}!", Toast.LENGTH_SHORT).show()
                    onRegisterSuccess()
                }
            }
            is Resource.Error<*> -> {
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
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = { Text("Nombre") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = { Text("Apellido") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Dirección") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Dirección") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = age,
                onValueChange = { newValue ->
                    if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                        age = newValue
                    }
                },
                label = { Text("Edad") },
                //leadingIcon = { Icon(Icons.Default.Numbers, contentDescription = "Edad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { /* Read-only, handled by DatePickerDialog */ },
                label = { Text("Fecha de Nacimiento (YYYY-MM-DD)") },
                leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Fecha de Nacimiento") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
                    .clickable { datePickerDialog.show() } // Abre el DatePicker al hacer clic
            )

            Button(
                onClick = {
                    val parsedAge = age.toIntOrNull() // Convertir la edad a Int
                    if (username.isNotEmpty() && password.isNotEmpty() && email.isNotEmpty()) {
                        registerViewModel.register(
                            username,
                            password,
                            firstName.ifEmpty { null }, // Enviar null si está vacío
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