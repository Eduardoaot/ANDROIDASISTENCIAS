package com.example.navigationguide.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navigationguide.responses.Resultado
import com.example.navigationguide.responses.Results
import com.example.navigationguide.network.RetrofitClient
import kotlinx.coroutines.launch

class ClasesViewModel : ViewModel() {
    private val _clases = mutableStateOf<List<Resultado>>(emptyList())
    val clases: State<List<Resultado>> = _clases
    private val _listaAsistencia = mutableStateOf<Results?>(null)
    val listaAsistencia: State<Results?> = _listaAsistencia

    init {
        obtenerClases()
    }

    private fun obtenerClases() {
        viewModelScope.launch {
            try {
                Log.d("API", "Obteniendo clases...")
                val response = RetrofitClient.apiService.cargarClases()
                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        if (responseBody.success) {
                            Log.d("API", "Datos cargados: ${responseBody.results}")
                            _clases.value = responseBody.results
                        }
                    }
                } else {
                    println("Error: ${response.code()} ${response.message()}")
                    Log.d("API", "Success == false en body")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun buscarClases(texto: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.buscarClases(texto)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.success) {
                            _clases.value = body.results
                        }
                    }
                } else {
                    println("Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun obtenerListaAsistencia(id: Int, fecha: String) {
        println("Obteniendo lista de asistencia")
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.mostrarListaAsistencia(id, fecha)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        if (body.success) {
                            _listaAsistencia.value = body.results
                            println(_listaAsistencia.value)
                        } else {
                            println("La respuesta fue fallida: success = false")
                        }
                    }
                } else {
                    println("Error HTTP: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                println("Error de red: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }


}
