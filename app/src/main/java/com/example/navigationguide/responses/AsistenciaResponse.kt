package com.example.navigationguide.responses

data class AsistenciaResponse(
    val mensaje: String,
    val errores: List<String>,
    val registrados: Int
)
