package com.example.tasked.ui.home.composable

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ExitToApp
//import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tasked.data.model.Task
import com.example.tasked.data.remote.RetrofitClient
import com.example.tasked.data.repository.TaskRepository
import com.example.tasked.ui.home.viewmodel.TaskViewModel
import com.example.tasked.ui.home.viewmodel.TaskViewModelFactory
import com.example.tasked.ui.theme.TaskedTheme
import com.example.tasked.utils.Resource
//import com.example.tasked.utils.Resource
import com.example.tasked.utils.SharedPreferencesManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BossDashboardScreen(
    username: String,
    onLogout: () -> Unit,
    onNavigateToProfile: () -> Unit, // Nuevo callback para navegar al perfil
    taskViewModel: TaskViewModel = viewModel(
        factory = TaskViewModelFactory(TaskRepository(RetrofitClient.apiService))
    )
) {
    val context = LocalContext.current
    val sharedPreferencesManager = remember { SharedPreferencesManager(context) }
    val authToken = sharedPreferencesManager.getAuthToken() ?: ""
    val currentUserId = sharedPreferencesManager.getUserId() // Obtener el ID del usuario actual
    val userRole = sharedPreferencesManager.getUserRole()
    var showEditTaskDialog by remember { mutableStateOf(false) }

    // Estados para controlar qué contenido mostrar
    var showCreateTaskDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }
    var currentView by remember { mutableStateOf<BossView>(BossView.MyTasks) }

    // Observa las tareas del ViewModel
    val myTasksResource by taskViewModel.myTasks.observeAsState()
    val assignedTasksResource by taskViewModel.assignedTasksByMe.observeAsState()
    val updateTaskStatusResult by taskViewModel.updateTaskStatusResult.observeAsState()
    val deleteTaskResult by taskViewModel.deleteTaskResult.observeAsState()
    val updateTaskResult by taskViewModel.updateTaskResult.observeAsState()

    // Cargar tareas al iniciar y cuando la vista cambia
    LaunchedEffect(currentView, authToken, updateTaskStatusResult, deleteTaskResult, updateTaskResult) { // Añadir updateTaskStatusResult como clave para refrescar
        if (authToken.isNotEmpty()) {
            when (currentView) {
                BossView.MyTasks -> taskViewModel.fetchMyTasks(authToken)
                BossView.AssignedTasks -> taskViewModel.fetchAssignedTasksByMe(authToken)
            }
        } else {
            Toast.makeText(context, "Token de autenticación no encontrado.", Toast.LENGTH_SHORT).show()
            onLogout() // Redirigir a login si no hay token
        }
        val deleteResult = deleteTaskResult
        if (deleteResult is Resource.Success<*>) {
            Toast.makeText(context, deleteResult.data as? String ?: "Tarea eliminada", Toast.LENGTH_SHORT).show()

        } else if (deleteResult is Resource.Error<*>) {
            Toast.makeText(context, deleteResult.message ?: "Error al eliminar", Toast.LENGTH_SHORT).show()

        }

        val editResult = updateTaskResult
        if (editResult is Resource.Success<*>) {
            Toast.makeText(context, "Tarea actualizada", Toast.LENGTH_SHORT).show()

        } else if (editResult is Resource.Error<*>) {
            Toast.makeText(context, editResult.message ?: "Error al actualizar", Toast.LENGTH_SHORT).show()

        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard de Jefe - Tasked") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) { // Botón de perfil
                        Icon(Icons.Filled.Person, contentDescription = "Perfil")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión")
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
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Mis Tareas") },
                    label = { Text("Mis Tareas") },
                    selected = currentView == BossView.MyTasks,
                    onClick = { currentView = BossView.MyTasks }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tareas Asignadas") },
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
                text = "Bienvenido/a, $username!",
                fontSize = 24.sp,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (currentView) {
                BossView.MyTasks -> TaskListSection(
                    title = "Mis Tareas Personales",
                    resource = myTasksResource,
                    emptyMessage = "No tienes tareas personales. ¡Crea una!",
                    taskViewModel = taskViewModel,
                    currentUserId = currentUserId,
                    userRole = userRole,
                    onDeleteTask = { taskId ->
                        if (authToken.isNotEmpty()) {
                            taskViewModel.deleteTask(authToken, taskId)
                        } else {
                            Toast.makeText(context, "Token no encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onEditTask= { task ->
                        taskToEdit = task
                        showEditTaskDialog = true
                    }
                )
                BossView.AssignedTasks -> TaskListSection(
                    title = "Tareas Asignadas por Ti",
                    resource = assignedTasksResource,
                    emptyMessage = "No has asignado ninguna tarea.",
                    taskViewModel = taskViewModel,
                    currentUserId = currentUserId,
                    userRole = userRole,
                    onDeleteTask = { taskId ->
                        if (authToken.isNotEmpty()) {
                            taskViewModel.deleteTask(authToken, taskId)
                        } else {
                            Toast.makeText(context, "Token no encontrado.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    onEditTask= { task ->
                        taskToEdit = task
                        showEditTaskDialog = true
                    }
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

    if (showEditTaskDialog && taskToEdit != null) {
        EditTaskDialog(
            taskToEdit = taskToEdit!!,
            onDismiss = {
                showEditTaskDialog = false
                taskToEdit = null
            },
            onTaskUpdated = {
                showEditTaskDialog = false
                taskToEdit = null
                if (currentView == BossView.MyTasks) {
                    taskViewModel.fetchMyTasks(authToken)
                } else if (currentView == BossView.AssignedTasks) {
                    taskViewModel.fetchAssignedTasksByMe(authToken)
                }
            },
            taskViewModel = taskViewModel
        )
    }
}



private sealed class BossView {
    object MyTasks : BossView()
    object AssignedTasks : BossView()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun BossDashboardScreenPreview() {
    TaskedTheme {
        BossDashboardScreen(
            username = "Mari",
            onLogout = {},
            onNavigateToProfile = {}
        )
    }
}
