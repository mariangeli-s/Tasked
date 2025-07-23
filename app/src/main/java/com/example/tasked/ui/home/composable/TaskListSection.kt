package com.example.tasked.ui.home.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
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

@Composable
fun TaskListSection(
    title: String,
    resource: Resource<List<Task>>?,
    emptyMessage: String,
    taskViewModel: TaskViewModel, // Pasa el ViewModel
    currentUserId: String? // Necesitamos el ID del usuario actual para determinar si es personal
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
                CircularProgressIndicator() // Modifier.align is not needed here
                // because Box's contentAlignment handles it
            }
        }
        is Resource.Success<*> -> {
            val tasks = (resource.data as? List<Task>) ?: emptyList()
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
                            onMarkAsCompleted = { taskId ->
                                //val authToken = taskViewModel.updateTaskStatusResult.value?.data?.token // Esto no es correcto, necesitas el token del SharedPreferences
                                // Para obtener el token, necesitas pasarlo desde el Composable padre o usar un SharedPreferencesManager aquí
                                val sharedPreferencesManager = SharedPreferencesManager(context)
                                val token = sharedPreferencesManager.getAuthToken()
                                if (token != null) {
                                    taskViewModel.markTaskAsCompleted(token, taskId)
                                    Toast.makeText(context, "Marcando tarea como completada...", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error: Token de autenticación no encontrado.", Toast.LENGTH_SHORT).show()
                                }
                            }
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
fun TaskItem(task: Task, currentUserId: String?, onMarkAsCompleted: (String) -> Unit) {
    val taskType = when {
        task.createdBy == currentUserId && task.assignedTo == null -> "Personal"
        task.assignedTo == currentUserId -> "Asignada a mí"
        task.createdBy == currentUserId && task.assignedTo != null -> "Asignada por mí" // Para jefes
        else -> "Desconocido"
    }

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

            if (task.status != "completed") {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onMarkAsCompleted(task.id) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Marcar como Completada")
                }
            }
        }
    }
}