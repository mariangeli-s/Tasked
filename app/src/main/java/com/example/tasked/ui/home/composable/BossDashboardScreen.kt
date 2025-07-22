package com.example.tasked.ui.home.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun BossDashboardScreen(
    username: String,
    onLogout: () -> Unit,
    taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(TaskRepository(RetrofitClient.apiService))
    )
) {
    val context = LocalContext.current
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) }
    val authToken = sharedPreferencesManager.getAuthToken() ?: ""

    // Estados para controlar qué contenido mostrar
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var currentView by remember { mutableStateOf<BossView>(BossView.MyTasks) }

    // Observa las tareas del ViewModel
    val myTasksResource by taskViewModel.myTasks.observeAsState()
    val assignedTasksResource by taskViewModel.assignedTasksByMe.observeAsState()

    // Cargar tareas al iniciar y cuando la vista cambia
    LaunchedEffect(currentView, authToken) {
        if (authToken.isNotEmpty()) {
            when (currentView) {
                BossView.MyTasks -> taskViewModel.fetchMyTasks(authToken)
                BossView.AssignedTasks -> taskViewModel.fetchAssignedTasksByMe(authToken)
            }
        } else {
            Toast.makeText(context, "Token de autenticación no encontrado.", Toast.LENGTH_SHORT).show()
            onLogout() // Redirigir a login si no hay token
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard de Jefe - Tasked") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateTaskDialog = true }) {
                Icon(Icons.Filled.Add, "Crear nueva tarea")
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Mis Tareas") },
                    label = { Text("Mis Tareas") },
                    selected = currentView == BossView.MyTasks,
                    onClick = { currentView = BossView.MyTasks }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.List, contentDescription = "Tareas Asignadas") },
                    label = { Text("Asignadas") },
                    selected = currentView == BossView.AssignedTasks,
                    onClick = { currentView = BossView.AssignedTasks }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Bienvenido, $username!",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (currentView) {
                BossView.MyTasks -> TaskListSection(
                    title = "Mis Tareas Personales",
                    resource = myTasksResource,
                    emptyMessage = "No tienes tareas personales. ¡Crea una!"
                )
                BossView.AssignedTasks -> TaskListSection(
                    title = "Tareas Asignadas por Ti",
                    resource = assignedTasksResource,
                    emptyMessage = "No has asignado ninguna tarea."
                )
            }
        }
    }

    if (showCreateTaskDialog) {
        CreateTaskDialog(
            onDismiss = { showCreateTaskDialog = false },
            onTaskCreated = {
                showCreateTaskDialog = false
                // Refrescar la lista de tareas relevante después de la creación
                if (currentView == BossView.MyTasks) {
                    taskViewModel.fetchMyTasks(authToken)
                } else if (currentView == BossView.AssignedTasks) {
                    taskViewModel.fetchAssignedTasksByMe(authToken)
                }
            },
            taskViewModel = taskViewModel,
            isBoss = true
        )
    }
}

private sealed class BossView {
    object MyTasks : BossView()
    object AssignedTasks : BossView()
}
