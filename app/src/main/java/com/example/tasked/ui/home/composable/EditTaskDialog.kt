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
import com.example.tasked.data.model.Task
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.data.repository.TaskRepository
import com.example.tasked.ui.home.viewmodel.TaskViewModel
import com.example.tasked.ui.home.viewmodel.TaskViewModelFactory
import com.example.tasked.utils.Resource
import com.example.tasked.utils.SharedPreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTaskDialog(
    taskToEdit: Task, // La tarea que se va a editar
    onDismiss: () -> Unit,
    onTaskUpdated: () -> Unit,
    taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(TaskRepository(RetrofitClient.apiService))
    )
) {
    val context = LocalContext.current
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) }
    val authToken = sharedPreferencesManager.getAuthToken() ?: ""
    val isBoss = sharedPreferencesManager.getUserRole() == "boss"

    var title by remember { mutableStateOf(taskToEdit.title) }
    var description by remember { mutableStateOf(taskToEdit.description) }
    var assignedToUserId: Int by remember { mutableStateOf(taskToEdit.assignedTo) }
    var assignedToUsername: String? by remember { mutableStateOf(taskToEdit.assignee) }

    val updateTaskResult by taskViewModel.updateTaskResult.observeAsState()
    val usersResource by taskViewModel.users.observeAsState()

    // Cargar usuarios si es jefe y la tarea ya está asignada o si quiere asignar
    LaunchedEffect(isBoss, authToken) {
        if (isBoss && authToken.isNotEmpty()) {
            taskViewModel.fetchUsers(authToken)
        }
    }

    // Manejar el resultado de la actualización de tarea
    LaunchedEffect(updateTaskResult) {
        val result = updateTaskResult
        when (result) {
            is Resource.Success<*> -> {
                Toast.makeText(context, "Tarea actualizada exitosamente!", Toast.LENGTH_SHORT).show()
                onTaskUpdated()
            }
            is Resource.Error<*> -> {
                val errorMessage = result.message ?: "Error desconocido al actualizar tarea"
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
                        text = "Editar Tarea",
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

                if (isBoss) { // Solo mostrar la opción de asignar si es jefe
                    Text(
                        text = "Asignar a (opcional):",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    when (usersResource) {
                        is Resource.Loading<*> -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                        is Resource.Success<*> -> {
                            val users = (usersResource as Resource.Success).data ?: emptyList()

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .selectable(
                                        selected = assignedToUserId == null,
                                        onClick = {
                                            assignedToUserId = null
                                            assignedToUsername = null
                                        },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = assignedToUserId == null,
                                    onClick = null
                                )
                                Text(
                                    text = "Tarea personal (para mí)",
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }

                            if (users.isNotEmpty()) {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 200.dp)
                                        .selectableGroup()
                                ) {
                                    items(users) { user ->
                                        Row(
                                            Modifier
                                                .fillMaxWidth()
                                                .selectable(
                                                    selected = user.id == assignedToUserId,
                                                    onClick = {
                                                        assignedToUserId = user.id
                                                        assignedToUsername = user.username
                                                    },
                                                    role = Role.RadioButton
                                                )
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            RadioButton(
                                                selected = user.id == assignedToUserId,
                                                onClick = null
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

                            OutlinedTextField(
                                value = assignedToUsername ?: "No asignado",
                                onValueChange = { /* Read-only */ },
                                label = { Text("Asignado actualmente") },
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            )

                        }
                        is Resource.Error<*> -> Text("Error al cargar usuarios: ${usersResource.message}", color = MaterialTheme.colorScheme.error)
                        null -> Text("Cargando usuarios...")
                    }
                }

                Button(
                    onClick = {
                        if (title.isNotEmpty() && description.isNotEmpty()) {
                            if (authToken.isNotEmpty()) {
                                taskViewModel.updateTask(authToken, taskToEdit.id.toString(), title, description, assignedToUserId)
                            } else {
                                Toast.makeText(context, "Token de autenticación no encontrado.", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Por favor, completa título y descripción", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = updateTaskResult !is Resource.Loading<*>
                ) {
                    if (updateTaskResult is Resource.Loading<*>) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}