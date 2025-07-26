<?php

use Illuminate\Foundation\Application;
use Illuminate\Foundation\Configuration\Exceptions;
use Illuminate\Foundation\Configuration\Middleware;

return Application::configure(basePath: dirname(__DIR__))
    ->withRouting(
        web: __DIR__.'/../routes/web.php',
        api: __DIR__.'/../routes/api.php',
        commands: __DIR__.'/../routes/console.php', // si tienes un archivo con comandos personalizados
        health: '/up'
    )
    ->withMiddleware(function (Middleware $middleware): void {
        // Añade el middleware de autenticación de Sanctum al grupo 'api'
        // Esto asegura que las rutas en routes/api.php puedan usar 'auth:sanctum'
        //$middleware->api(prepend: [
            //\Laravel\Sanctum\Http\Middleware\Authenticate::class,
        //]);
        $middleware->api(prepend: [
            \Laravel\Sanctum\Http\Middleware\EnsureFrontendRequestsAreStateful::class,
        ]);



        // Si tienes problemas con CSRF en APIs (lo cual no deberías en APIs puras),
        // puedes asegurarte de que este middleware no se aplique a tus rutas API.
        // Por defecto, Laravel 11 ya lo maneja bien para APIs.
        // $middleware->web(append: [
        //     \Illuminate\Foundation\Http\Middleware\VerifyCsrfToken::class,
        // ]);
    })
    ->withExceptions(function (Exceptions $exceptions): void {
        //
    })->create();