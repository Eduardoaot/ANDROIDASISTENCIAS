package com.example.navigationguide

data class Resultado(
    val id: Int,
    val grupo: String,
    val dia: String,
    val hora: String,
    val materia: String,
    val folio: String
)

data class ClasesResponse(
    val success: Boolean,
    val results: List<Resultado>
)

