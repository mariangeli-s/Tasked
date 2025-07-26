<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use App\Models\Task;
use App\Models\User; // Para buscar usuarios a asignar
use Illuminate\Support\Facades\Auth;
use Illuminate\Validation\ValidationException;

class TaskController extends Controller
{
    public function store(Request $request)
    {
        try {
            $validatedData = $request->validate([
                'title' => 'required|string|max:255',
                'description' => 'required|string',
                'assignedTo' => 'nullable|exists:users,id', // Valida que el ID exista en la tabla users
            ]);
        } catch (ValidationException $e) {
            return response()->json(['errors' => $e->errors()], 422);
        }

        $user = Auth::user();

        // Si se intenta asignar y el usuario no es jefe, denegar
        if ($request->has('assignedTo') && $user->role !== 'boss') {
            return response()->json(['message' => 'Solo los jefes pueden asignar tareas a otros usuarios.'], 403);
        }

        $task = Task::create([
            'title' => $validatedData['title'],
            'description' => $validatedData['description'],
            'created_by' => $user->id,
            'assigned_to' => $validatedData['assignedTo'] ?? null, // Asigna si se proporcionó, de lo contrario null
            'status' => 'pending', // Estado inicial
        ]);

        // Cargar relaciones si quieres devolver el nombre del creador/asignado
        $task->load('creator', 'assignee');

        return response()->json($task, 201); // 201 Created
    }

    public function myTasks(Request $request)
    {
        $user = Auth::user();

        // Tareas creadas por el usuario O tareas asignadas al usuario
        $tasks = Task::where('created_by', $user->id)
                     ->orWhere('assigned_to', $user->id)
                     ->with('creator', 'assignee') // Cargar relaciones
                     ->get();

        // Formatear la respuesta para que coincida con el modelo Android
        $formattedTasks = $tasks->map(function($task) {
            return [
                'id' => $task->id,
                'title' => $task->title,
                'description' => $task->description,
                'assignedTo' => $task->assigned_to, // Solo el ID (puede ser null)
                'assignee' => $task->assignee ? $task->assignee->username : null, // Solo el username
                'status' => $task->status,
                'createdBy' => $task->created_by,
                'creator' => $task->creator ? $task->creator->username : null, // Solo el username del creador
                'createdAt' => $task->created_at,
                'updatedAt' => $task->updated_at,
            ];
        });

        return response()->json($formattedTasks);
    }

    public function assignedByMe(Request $request)
    {
        $user = Auth::user();

        if ($user->role !== 'boss') {
            return response()->json(['message' => 'Acceso denegado. Solo los jefes pueden ver las tareas que asignaron.'], 403);
        }

        $tasks = Task::where('created_by', $user->id)
                     ->whereNotNull('assigned_to') // Solo tareas que fueron asignadas
                     ->with('creator', 'assignee')
                     ->get();

        // Formatear la respuesta igual que en myTasks
        $formattedTasks = $tasks->map(function($task) {
            return [
                'id' => $task->id,
                'title' => $task->title,
                'description' => $task->description,
                'assignedTo' => $task->assigned_to,
                'assignee' => $task->assignee ? $task->assignee->username : null,
                'status' => $task->status,
                'createdBy' => $task->created_by,
                'creator' => $task->creator ? $task->creator->username : null,
                'createdAt' => $task->created_at,
                'updatedAt' => $task->updated_at,
            ];
        });

        return response()->json($formattedTasks);
    }

    public function updateStatus(Request $request, Task $task) 
    {
        try {
            $validatedData = $request->validate([
                'status' => 'required|in:pending,completed', // Solo permite estos estados
            ]);
        } catch (ValidationException $e) {
            return response()->json(['errors' => $e->errors()], 422);
        }

        $user = Auth::user();

        // Lógica de autorización y manejo de estados mejorada
        if ($user->role === 'boss') {
            // Un jefe SÓLO puede cambiar el estado de sus PROPIAS tareas personales (no asignadas)
            if ($task->created_by === $user->id && $task->assigned_to === null) {
                // Jefe actualiza su tarea personal
                // Proceder con la actualización
            } else {
                // Jefe no puede actualizar tareas que asignó a otros, ni tareas que no son suyas
                return response()->json(['message' => 'Los jefes solo pueden actualizar el estado de sus tareas personales.'], 403);
            }
        } elseif ($user->role === 'employee') {
            // Un empleado puede cambiar el estado de sus tareas personales O de las que le fueron asignadas
            if ($task->created_by === $user->id || $task->assigned_to === $user->id) {
                // Empleado actualiza su tarea personal o una asignada a él
                // Proceder con la actualización
            } else {
                // Empleado no puede actualizar tareas que no le pertenecen
                return response()->json(['message' => 'No autorizado para actualizar esta tarea.'], 403);
            }
        } else {
            // Rol desconocido o no autenticado (aunque el middleware Auth:sanctum ya debería manejar esto)
            return response()->json(['message' => 'No autorizado para actualizar esta tarea.'], 403);
        }

        // Si la autorización es exitosa, procede a actualizar el estado
        $task->status = $validatedData['status'];
        $task->save();

        $task->load('creator', 'assignee'); // Recargar para devolver relaciones actualizadas

        return response()->json($task);
    }

    ///
    public function update(Request $request, Task $task) // Método para edición general
    {
        $user = Auth::user();

        if ($task->created_by !== $user->id) {
            return response()->json(['message' => 'No autorizado para editar esta tarea. Solo el creador puede editar.'], 403);
        }

        try {
            $validatedData = $request->validate([
                'title' => 'required|string|max:255',
                'description' => 'required|string',
                'assignedTo' => 'nullable|exists:users,id',
            ]);
        } catch (ValidationException $e) {
            return response()->json(['errors' => $e->errors()], 422);
        }

        if ($request->has('assignedTo') && $user->role !== 'boss') {
            return response()->json(['message' => 'Solo los jefes pueden asignar tareas a otros usuarios.'], 403);
        }

        $task->title = $validatedData['title'];
        $task->description = $validatedData['description'];
        $task->assigned_to = $validatedData['assignedTo'] ?? null;

        $task->save();
        $task->load('creator', 'assignee');
        return response()->json($task);
    }

    public function assign(Request $request, Task $task)
    {
        $user = Auth::user();

        if ($user->role !== 'boss') {
            return response()->json(['message' => 'No autorizado. Solo los jefes pueden asignar tareas.'], 403);
        }

        if ($task->created_by !== $user->id) {
            return response()->json(['message' => 'No autorizado. Solo puedes asignar tareas que creaste.'], 403);
        }

        try {
            $validatedData = $request->validate([
                'assignedTo' => 'nullable|exists:users,id',
            ]);
        } catch (ValidationException $e) {
            return response()->json(['errors' => $e->errors()], 422);
        }

        $task->assigned_to = $validatedData['assignedTo'] ?? null;
        $task->save();
        $task->load('creator', 'assignee');

        return response()->json($task);
    }

    public function destroy(Task $task)
    {
        $user = Auth::user();

        $isAuthorized = false;
        if ($task->created_by === $user->id) {
            $isAuthorized = true;
        } elseif ($user->role === 'boss') { // Jefe puede borrar cualquier tarea
            $isAuthorized = true;
        }

        if (!$isAuthorized) {
            return response()->json(['message' => 'No autorizado para eliminar esta tarea.'], 403);
        }

        $task->delete();
        return response()->json(['message' => 'Tarea eliminada exitosamente.'], 200);
    }
    ///

    public function getUsersForAssignment(Request $request)
    {
        $user = Auth::user();

        if ($user->role !== 'boss') {
            return response()->json(['message' => 'Acceso denegado. Solo los jefes pueden ver la lista de usuarios.'], 403);
        }

        // Obtener solo empleados para asignación
        $employees = User::where('role', 'employee')
                         ->select('id', 'username', 'email') // Selecciona solo los campos necesarios
                         ->get();

        return response()->json($employees);
    }
}
