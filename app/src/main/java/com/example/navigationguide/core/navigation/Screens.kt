package com.example.navigationguide.core.navigation

import android.os.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost

@Serializable
object Home

@Serializable
data class Scanner(val materia: String, val grupo: String, val hora: String)

@Serializable
data class ListaAsistencia(val idGrupo: Int)

@Serializable
object Splash

@Serializable
object Error

@Serializable
object Data

