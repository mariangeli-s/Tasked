package com.example.tasked.ui.home.composable

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tasked.data.model.Task
import com.example.tasked.utils.Resource
import com.example.tasked.ui.home.viewmodel.TaskViewModel
import com.example.tasked.ui.theme.TaskedTheme
import com.example.tasked.utils.SharedPreferencesManager
import androidx.compose.runtime.remember

@Composable
fun TaskListSection(
    title: String,
    resource: Resource<List<Task>>?,
    emptyMessage: String,
    taskViewModel: TaskViewModel, // Pasa el ViewModel
    currentUserId: Int?,
    userRole: String?,
    onEditTask: (Task) ->Unit,
    onDeleteTask: (String) ->Unit
) {
    val context = LocalContext.current

    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    when (resource) {
        is Resource.Loading<*> -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth() // Or .fillMaxSize() if you want it to take full screen space
                    .padding(16.dp), // Optional padding
                contentAlignment = Alignment.Center // Centers content in both axes
            ) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success<*> -> {
            val tasks = (resource as? List<Task>) ?: emptyList()
            if (tasks.isEmpty()) {
                Text(emptyMessage, style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 60.dp) // Espacio para FAB
                ) {
                    items(tasks) { task ->
                        TaskItem(
                            task = task,
                            currentUserId = currentUserId,
                            userRole = userRole,
                            onMarkAsCompleted = { taskId ->
                                val sharedPreferencesManager = SharedPreferencesManager(context)
                                val token = sharedPreferencesManager.getAuthToken()
                                if (token != null) {
                                    taskViewModel.markTaskAsCompleted(token, taskId)
                                    Toast.makeText(context, "Marcando tarea como completada...", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error: Token de autenticación no encontrado.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onEditTask = onEditTask,
                            onDeleteTask = onDeleteTask
                        )
                    }
                }
            }
        }
        is Resource.Error<*> -> {
            Text("Error al cargar tareas: ${resource.message}", color = MaterialTheme.colorScheme.error)
        }
        null -> {
            Text("Cargando tareas...", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun TaskItem(task: Task,
             currentUserId: Int?,
             userRole: String?,
             onMarkAsCompleted: (String) -> Unit,
             onDeleteTask: (String) -> Unit,
             onEditTask: (Task) -> Unit
) {
    val context = LocalContext.current
    val sharedPreferencesManager = SharedPreferencesManager(context)
    var showDeleteConfirmDialog = remember { mutableStateOf(false) }

    val taskType = when {
        task.createdBy == currentUserId && task.assignedTo == null -> "Personal"
        task.assignedTo == currentUserId -> "Asignada a mí"
        task.createdBy == currentUserId && task.assignedTo != null -> "Asignada por mí" // Para jefes
        else -> "Desconocido"
    }

    // Lógica para determinar si mostrar el botón "Marcar como Completada"
    val showMarkAsCompletedButton = remember(task, currentUserId, userRole) { // Usar currentUserIdInt here
        if (task.status == "completed") {
            false // If already completed, don't show the button
        } else {
            when (userRole) {
                "boss" -> {
                    // Boss can only mark their OWN personal tasks (not assigned to others)
                    task.createdBy == currentUserId && task.assignedTo == null
                }
                "employee" -> {
                    // Employee can mark their OWN personal tasks OR tasks ASSIGNED TO THEM
                    task.createdBy == currentUserId || task.assignedTo == currentUserId
                }
                else -> false // Unknown role, don't show button
            }
        }
    }

    //MOSTRAR BOTON BORRAR
    val showDeleteButton = remember(task, currentUserId, userRole) {
        task.createdBy == currentUserId || userRole == "boss"
    }

    val showEditButton = remember(task, currentUserId, userRole) {
        task.createdBy == currentUserId
    }

    Log.d("TaskItemInfo", "Task ID: ${task.id}, Status: ${task.status}")
    Log.d("TaskItemInfo", "Current User ID: $currentUserId, User Role: $userRole")
    Log.d("TaskItemInfo", "Task CreatedBy: ${task.createdBy}, Task AssignedTo: ${task.assignedTo}")


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Tipo: $taskType",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Creada por: ${task.createdBy}", // Idealmente, mostrar el username
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            task.assignedTo?.let {
                Text(
                    text = "Asignada a: $it", // Idealmente, mostrar el username
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Estado: ${task.status}",
                style = MaterialTheme.typography.bodySmall,
                color = if (task.status == "completed") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
            )

            //botones
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ){
                if (showMarkAsCompletedButton){
                    Button(onClick = { onMarkAsCompleted(task.id.toString()) }) {
                        Text("Completar")
                    }
                }
                if (showEditButton) {
                    Button(
                        onClick = { onEditTask(task) },
                        modifier = Modifier.padding(end = 8.dp)
                    ){
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                        Spacer(Modifier.width(4.dp))
                        Text("Editar")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))
                if (showDeleteButton) {
                    Button(
                        onClick = { onEditTask(task) },
                        colors = ButtonDefaults.buttonColors(containerColor=MaterialTheme.colorScheme.error)
                    ){
                        Icon(Icons.Default.Delete, contentDescription = "Borrar")
                        Spacer(Modifier.width(4.dp))
                        Text("Borrar")
                    }
                }
            }

            if (showMarkAsCompletedButton) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onMarkAsCompleted(task.id.toString()) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Marcar como Completada")
                }
            }
        }
    }
    if (showDeleteConfirmDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog.value = false },
            title = {
                Text(text = "Confirmar eliminación")
            },
            text = {
                Text(text = "¿Estás seguro de que deseas eliminar esta tarea?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTask(task.id.toString())
                        showDeleteConfirmDialog.value  = false
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteConfirmDialog.value = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TaskItemPreview() {
    TaskedTheme {
    }
}
