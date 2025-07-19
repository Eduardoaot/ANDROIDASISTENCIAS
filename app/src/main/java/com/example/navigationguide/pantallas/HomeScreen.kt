package com.example.navigationguide.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navigationguide.R
import com.example.navigationguide.core.navigation.ListaAsistencia
import com.example.navigationguide.core.navigation.Scanner
import com.example.navigationguide.viewmodel.ClasesViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: ClasesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val clases = viewModel.clases.value
    var textoBusqueda by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Barra superior
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(Color(0xFF3466BB))
                .padding(top = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_scanli_pequenio),
                    contentDescription = "Logo",
                    modifier = Modifier.size(50.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scanli",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = Color.White
                )
            }
        }



        Spacer(modifier = Modifier.height(16.dp))

        // Barra de búsqueda
        OutlinedTextField(
            value = textoBusqueda,
            onValueChange = {
                textoBusqueda = it
                viewModel.buscarClases(it) // Ejecuta búsqueda
            },
            placeholder = { Text("Buscar...") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (clases.isEmpty()) {
            Text(
                text = "No hay clases para mostrar",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
        } else {
            clases.forEach { clase ->
                ClassCard(
                    materia = clase.materia,
                    folio = "Folio #${clase.folio}",
                    navController = navController,
                    onVerLista = {},
                    hora = clase.hora,
                    grupo = clase.grupo,
                    idGrupo = clase.id
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

const val ScannerRoute = "scanner"

@Composable
fun ClassCard(
    materia: String,
    folio: String,
    hora: String,
    grupo: String,
    idGrupo: Int,
    navController: NavController,
    onVerLista: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = materia,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = folio,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hora: $hora",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Grupo: $grupo",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de Registrar Asistencia
                Button(
                    onClick = {
                        navController.navigate(
                            Scanner(materia = materia, grupo = grupo, hora = hora)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0CC0DF)),
                    modifier = Modifier
                        .weight(2.4f)
                        .height(85.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.list),
                            contentDescription = "Lista Icono",
                            modifier = Modifier.size(45.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Registrar asistencia",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        navController.navigate(
                            ListaAsistencia(idGrupo = idGrupo)
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4a56d1)),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(85.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.check), // ícono de palomita
                            contentDescription = "Palomita",
                            modifier = Modifier.size(45.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Lista",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                    }
                }
            }

        }
    }
}


