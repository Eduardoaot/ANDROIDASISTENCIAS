package com.example.navigationguide.pantallas

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navigationguide.responses.Clase
import com.example.navigationguide.viewmodel.ClasesViewModel

@Composable
fun ListaScreen(
    navController :NavController,
    idGrupo: Int,
    viewModel: ClasesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val fecha = "2025-06-30"
    val listaAsistencia = viewModel.listaAsistencia.value
    // Aquí podrías llamar al ViewModel:
    LaunchedEffect(Unit) {
        viewModel.obtenerListaAsistencia(idGrupo, fecha)
        println("Llamando a obtenerListaAsistencia con id=$idGrupo y fecha=$fecha")

    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF2F2F2))
    ) {
        // Encabezado azul con ícono y título

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFF3466BB))
                .padding(start = 12.dp, end = 12.dp, top = 40.dp)
        ) {
            // Columna izquierda: Botón de regreso
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart),
                verticalArrangement = Arrangement.Center
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                }
            }
            //Tremendo
            // Columna central: Título
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Lista de asistencia",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    fontWeight = FontWeight.Bold
                )
            }
        }




        val listaAsistencia = viewModel.listaAsistencia.value
        val clase = listaAsistencia?.clase

        if (clase != null) {
            TituloClase(clase = clase, fecha)
        } else {
            // Mientras carga o si es null puedes mostrar algo
            Text("Cargando info de la clase...")
        }


        val alumnos = listaAsistencia?.alumnos ?: emptyList()

        val asistenciaEstados = remember(alumnos) {
            mutableStateListOf<Boolean>().apply {
                alumnos.forEach { add(it.asistencia) }
            }
        }

        if (alumnos.isNotEmpty() && asistenciaEstados.size == alumnos.size) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF4D6FD6))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TableCell("No", Color.White, FontWeight.Bold, Modifier.width(30.dp))
                        TableCell("Nombre", Color.White, FontWeight.Bold, Modifier.weight(1f))
                        TableCell("Matrícula", Color.White, FontWeight.Bold, Modifier.width(90.dp))
                        TableCell("Asistencia", Color.White, FontWeight.Bold, Modifier.width(90.dp))
                    }
                }

                items(alumnos.size) { index ->
                    val alumno = alumnos[index]

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (index % 2 == 0) Color.White else Color(0xFFF0F0F0))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TableCell("${alumno.numeroLista}", Color.Black, FontWeight.Normal, Modifier.width(30.dp))
                        TableCell(alumno.matricula, Color.Black, FontWeight.Normal, Modifier.weight(1f))
                        TableCell(alumno.nombre, Color.Black, FontWeight.Normal, Modifier.width(90.dp))

                        Checkbox(
                            checked = asistenciaEstados[index],
                            onCheckedChange = { checked ->
                                asistenciaEstados[index] = checked
                            },
                            modifier = Modifier.width(90.dp)
                        )
                    }
                }
            }
        } else {
            // Mostrar un mensaje de carga o vacío
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Cargando lista de asistencia...", color = Color.Gray)
            }
        }



        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0061F2)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp) // Ajusta la altura si quieres
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Actualizar lista", color = Color.White)
            }
        }
        Spacer(modifier = Modifier.height(40.dp))

    }
}

@Composable
fun TituloClase(clase: Clase, Fecha: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = clase.materia,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0061F2)
        )
        Text(
            text = "Folio ${clase.folio} ${clase.grupo.trim()} ${clase.hora} ${clase.dia} ",
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0061F2),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = "Fecha:",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0061F2)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "${Fecha}")  // Aquí puedes pasar la fecha que desees dinámicamente
        }
    }
}


@Composable
fun TableCell(text: String, color: Color, weight: FontWeight, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = color,
        fontWeight = weight,
        fontSize = 14.sp,
        modifier = modifier
    )
}
