package com.example.tasked.ui.home.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.data.repository.TaskRepository
import com.example.tasked.ui.home.viewmodel.TaskViewModel
import com.example.tasked.ui.home.viewmodel.TaskViewModelFactory
import com.example.tasked.utils.Resource
import com.example.tasked.utils.SharedPreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskDialog(
    onDismiss: () -> Unit,
    onTaskCreated: () -> Unit,
    taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(TaskRepository(RetrofitClient.apiService))
    ),
    isBoss: Boolean // Indica si el usuario actual es un jefe
) {
    val context = LocalContext.current
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) }
    val authToken = sharedPreferencesManager.getAuthToken() ?: ""

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var assignedToUser: String? by remember { mutableStateOf(null) } // ID del usuario asignado

    val createTaskResult by taskViewModel.createTaskResult.observeAsState()
    val usersResource by taskViewModel.users.observeAsState()

    // Cargar usuarios si es jefe
    LaunchedEffect(isBoss, authToken) {
        if (isBoss && authToken.isNotEmpty()) {
            taskViewModel.fetchUsers(authToken)
        }
    }

    // Manejar el resultado de la creación de tarea
    LaunchedEffect(createTaskResult) {
        when (createTaskResult) {
            is Resource.Success<*> -> {
                Toast.makeText(context, "Tarea creada exitosamente!", Toast.LENGTH_SHORT).show()
                onTaskCreated()
            }
            is Resource.Error<*> -> {
                val errorMessage = createTaskResult?.message ?: "Error desconocido al crear tarea"
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            else -> {} // Loading o null
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Crear Nueva Tarea",
                        style = MaterialTheme.typography.headlineSmall
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título de la tarea") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción de la tarea") },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    minLines = 3
                )

                if (isBoss) {
                    Text(
                        text = "Asignar a (opcional):",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    when (usersResource) {
                        is Resource.Loading<*> -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        is Resource.Success<*> -> {
                            val users = (usersResource as Resource.Success).data ?: emptyList()
                            if (users.isNotEmpty()) {
                                // Opción para tarea personal (no asignada)
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = assignedToUser == null,
                                            onClick = { assignedToUser = null },
                                            role = Role.RadioButton
                                        )
                                        .padding(vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = assignedToUser == null,
                                        onClick = null // null because the row handles the click
                                    )
                                    Text(
                                        text = "Tarea personal (para mí)",
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }

                                // Lista de usuarios para asignar
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp) // Limita la altura de la lista
                                        .selectableGroup()
                                ) {
                                    items(users) { user ->
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .selectable(
                                                    selected = user.id == assignedToUser,
                                                    onClick = { assignedToUser = user.id },
                                                    role = Role.RadioButton
                                                )
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = user.id == assignedToUser,
                                                onClick = null // null because the row handles the click
                                            )
                                            Text(
                                                text = user.username,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.padding(start = 8.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            } else {
                                Text("No hay empleados disponibles para asignar.", modifier = Modifier.padding(bottom = 16.dp))
                            }
                        }
                        is Resource.Error<*> -> Text("Error al cargar usuarios: ${usersResource?.message}", color = MaterialTheme.colorScheme.error)
                        null -> Text("Cargando usuarios...")
                    }
                }

                Button(
                    onClick = {
                        if (title.isNotEmpty() && description.isNotEmpty()) {
                            taskViewModel.createTask(authToken, title, description, assignedToUser)
                        } else {
                            Toast.makeText(context, "Por favor, completa título y descripción", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = createTaskResult !is Resource.Loading<*>
                ) {
                    if (createTaskResult is Resource.Loading<*>) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Crear Tarea")
                    }
                }
            }
        }
    }
}