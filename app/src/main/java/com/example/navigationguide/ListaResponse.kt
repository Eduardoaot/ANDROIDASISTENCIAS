package com.example.navigationguide

data class ListaResponse(
    val success: Boolean,
    val results: Results
)

data class Results(
    val clase: Clase,
    val alumnos: List<Alumno>
)

data class Clase(
    val id: Int,
    val grupo: String,
    val dia: String,
    val hora: String,
    val materia: String,
    val folio: String
)

data class Alumno(
    val numeroLista: Int,
    val nombre: String,
    val matricula: String,
    val asistencia: Boolean,
    val idMasterClases: Int
)
