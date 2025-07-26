<?php

namespace App\Models;

// use Illuminate\Contracts\Auth\MustVerifyEmail;
use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Illuminate\Notifications\Notifiable;
use Laravel\Sanctum\HasApiTokens;

class User extends Authenticatable
{
    /** @use HasFactory<\Database\Factories\UserFactory> */
    use HasApiTokens, HasFactory, Notifiable;
    /**
     * The attributes that are mass assignable.
     *
     * @var list<string>
     */
    protected $fillable = [
        'username', // Asegúrate de que este campo exista en tu migración
        'email',
        'password',
        'first_name', // Nuevo
        'last_name',  // Nuevo
        'phone',      // Nuevo
        'address',    // Nuevo
        'age',        // Nuevo
        'date_of_birth', // Nuevo
        'role',       // Nuevo
    ];

    /**
     * The attributes that should be hidden for serialization.
     *
     * @var list<string>
     */
    protected $hidden = [
        'password',
        'remember_token',
    ];

    /**
     * Get the attributes that should be cast.
     *
     * @return array<string, string>
     */
    protected function casts(): array
    {
        return [
            'email_verified_at' => 'datetime',
            'password' => 'hashed',
            'date_of_birth' => 'date',
        ];
    }

    // Relación para tareas creadas por este usuario
    public function createdTasks()
    {
        return $this->hasMany(Task::class, 'created_by');
    }

    // Relación para tareas asignadas a este usuario
    public function assignedTasks()
    {
        return $this->hasMany(Task::class, 'assigned_to');
    }
}
