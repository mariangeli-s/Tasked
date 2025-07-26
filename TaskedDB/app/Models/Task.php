<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Task extends Model
{
    use HasFactory;

    /**
     * The attributes that are mass assignable.
     *
     * @var array<int, string>
     */
    protected $fillable = [
        'title',
        'description',
        'created_by',
        'assigned_to',
        'status',
    ];

    /**
     * The attributes that should be cast.
     *
     * @var array<string, string>
     */
    protected $casts = [
        'assigned_to' => 'integer', // Asegurarse de que se castée a entero
    ];

    // Relación con el usuario que creó la tarea
    public function creator()
    {
        return $this->belongsTo(User::class, 'created_by');
    }

    // Relación con el usuario al que se le asignó la tarea (opcional)
    public function assignee()
    {
        return $this->belongsTo(User::class, 'assigned_to');
    }
}