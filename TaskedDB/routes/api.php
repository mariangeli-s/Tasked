<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\Api\AuthController;
use App\Http\Controllers\Api\TaskController;

// Rutas de autenticación (no protegidas por token)
Route::prefix('v1')->group(function () {
    Route::post('auth/register', [AuthController::class, 'register']);
    Route::post('auth/login', [AuthController::class, 'login']);
});

// Rutas protegidas por autenticación (requieren un token de Sanctum válido)
Route::middleware('auth:sanctum')->prefix('v1')->group(function () {
    Route::post('auth/logout', [AuthController::class, 'logout']); // Añadir logout


    Route::post('tasks', [TaskController::class, 'store']); // Crear tarea
    Route::get('tasks/my', [TaskController::class, 'myTasks']); // Mis tareas (personales o asignadas a mí)
    Route::get('tasks/assigned-by-me', [TaskController::class, 'assignedByMe']); // Tareas asignadas por el jefe
    Route::patch('tasks/{task}', [TaskController::class, 'updateStatus']); // Actualizar estado de tarea
    Route::put('tasks/{task}', [TaskController::class, 'update']); // NUEVO: Actualizar tarea general (título, descripción, asignación)
    Route::patch('tasks/{task}/assign', [TaskController::class, 'assign']); // Asignar/Reasignar una tarea existente
    Route::delete('tasks/{task}', [TaskController::class, 'destroy']); // Eliminar una tarea

    Route::get('users', [TaskController::class, 'getUsersForAssignment']); // Obtener usuarios para asignación
});
