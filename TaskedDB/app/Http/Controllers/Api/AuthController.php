<?php

namespace App\Http\Controllers\Api;

use App\Http\Controllers\Controller;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use App\Models\User;
use Illuminate\Validation\ValidationException;

class AuthController extends Controller
{
    public function register(Request $request)
    {
        try {
            $validatedData = $request->validate([
                'username' => 'required|string|max:255|unique:users,username',
                'password' => 'required|string|min:6',
                'firstName' => 'nullable|string|max:255',
                'lastName' => 'nullable|string|max:255',
                'email' => 'required|string|email|max:255|unique:users,email',
                'phone' => 'nullable|string|max:20',
                'address' => 'nullable|string|max:255',
                'age' => 'nullable|integer|min:0',
                'dateOfBirth' => 'nullable|date_format:Y-m-d', // Asegúrate del formato
            ]);
        } catch (ValidationException $e) {
            return response()->json(['errors' => $e->errors()], 422);
        }

        // Determinar el rol: el primer usuario registrado es automáticamente jefe
        $role = (User::count() == 0) ? 'boss' : 'employee';

        $user = User::create([
            'username' => $validatedData['username'],
            'email' => $validatedData['email'],
            'password' => Hash::make($validatedData['password']),
            'first_name' => $validatedData['firstName'] ?? null,
            'last_name' => $validatedData['lastName'] ?? null,
            'phone' => $validatedData['phone'] ?? null,
            'address' => $validatedData['address'] ?? null,
            'age' => $validatedData['age'] ?? null,
            'date_of_birth' => $validatedData['dateOfBirth'] ?? null,
            'role' => $role,
        ]);

        // Generar un token para el usuario
        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user' => [
                'id' => $user->id,
                'username' => $user->username,
                'role' => $user->role,
                'firstName' => $user->first_name,
                'lastName' => $user->last_name,
                'email' => $user->email,
                'phone' => $user->phone,
                'address' => $user->address,
                'age' => $user->age,
                'dateOfBirth' => $user->date_of_birth ? $user->date_of_birth->format('Y-m-d') : null,
            ]
        ], 201); // 201 Created
    }

    public function login(Request $request)
    {
        $credentials = $request->validate([
            'username' => 'required|string',
            'password' => 'required|string',
        ]);

        if (!Auth::attempt(['username' => $credentials['username'], 'password' => $credentials['password']])) {
            return response()->json(['message' => 'Credenciales inválidas'], 401);
        }

        $user = Auth::user();
        // Eliminar tokens antiguos si solo quieres uno activo por dispositivo
        $user->tokens()->delete();

        // Generar un nuevo token
        $token = $user->createToken('auth_token')->plainTextToken;

        return response()->json([
            'token' => $token,
            'user' => [
                'id' => $user->id,
                'username' => $user->username,
                'role' => $user->role,
                'firstName' => $user->first_name,
                'lastName' => $user->last_name,
                'email' => $user->email,
                'phone' => $user->phone,
                'address' => $user->address,
                'age' => $user->age,
                'dateOfBirth' => $user->date_of_birth ? $user->date_of_birth->format('Y-m-d') : null,
            ]
        ]);
    }

    public function logout(Request $request)
    {
        $request->user()->currentAccessToken()->delete();
        return response()->json(['message' => 'Sesión cerrada exitosamente']);
    }
}