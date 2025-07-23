package com.example.tasked.ui.home.composable

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tasked.data.model.Task
import com.example.tasked.utils.Resource

@Composable
fun TaskListSection(
    title: String,
    resource: Resource<List<Task>>?,
    emptyMessage: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.padding(vertical = 16.dp)
    )

    when (resource) {
        is Resource.Loading<*> -> {
            // Wrap CircularProgressIndicator in a Column to use .align
            Column(
                modifier = Modifier.fillMaxWidth(), // Occupy available width
                horizontalAlignment = Alignment.CenterHorizontally // Center children horizontally by default
            ) {
                CircularProgressIndicator() // .align(Alignment.CenterHorizontally)
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
                        TaskItem(task = task)
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
fun TaskItem(task: Task) {
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
                text = "Creada por: ${task.createdBy}", // Aquí podrías mostrar el username si lo recuperas
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            task.assignedTo?.let {
                Text(
                    text = "Asignada a: $it", // Aquí podrías mostrar el username si lo recuperas
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "Estado: ${task.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
