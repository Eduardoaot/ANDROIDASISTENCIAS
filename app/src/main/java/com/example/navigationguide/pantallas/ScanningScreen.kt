package com.example.navigationguide.pantallas

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.example.navigationguide.viewmodel.ClasesViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import java.util.concurrent.Executor
import android.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.crypto.BadPaddingException
import javax.crypto.IllegalBlockSizeException
import javax.crypto.NoSuchPaddingException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.launch


@Composable
fun ScanningScreen(
    navController: NavHostController,
    materia: String,
    grupo: String,
    hora: String,
    viewModel: ClasesViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val clases = viewModel.clases.value
    val mandarAsistencia = remember { mutableStateListOf<Int>() }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.obtenerContraseniaSemanal()

    }

    val clave = "1234567890123456"  // 16 chars = 128 bits
    val iv = "abcdefghijklmnop"
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    val barcodeScanner = remember { BarcodeScanning.getClient() }
    val cameraExecutor = remember { Dispatchers.Default.asExecutor() }

    var scanResult by remember { mutableStateOf("Escanea el código QR") }
    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
    }

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }
    fun procesarInformacionQR(contenido: String) {
        println("Contenido del QR leído: $contenido")
    }

    var scanRawResult by remember { mutableStateOf("") }
    var scanValidResult by remember { mutableStateOf("Escanea el código QR") }
    val puedeAceptar = scanValidResult.startsWith("Alumno:")

    fun textoANumero(texto: String): Long {
        val bytes = texto.toByteArray(Charsets.UTF_8)
        var resultado = 0L
        for (b in bytes) {
            resultado = resultado * 256 + (b.toInt() and 0xFF)
        }
        return resultado
    }

    fun procesarResultado(scan: String, grupoEsperado: String, contrasenia: String): String {
        val partes = scan.split(",").map { it.trim() }
        println("El valor de contrasenia es:$contrasenia")
        if (partes.size < 5) {
            return "❌ QR inválido o incompleto"
        }

        val idMaster = partes[0]
        if (idMaster.length <= 10) {
            return "❌ idMaster inválidom MUY posiblemente modificado"
        }

        val idMasterRecortado = idMaster.substring(5, idMaster.length - 5)
        val idMasterNumero = idMasterRecortado.toLongOrNull()
        val numero = contrasenia.toLongOrNull()
        var idMasterFinal: Long = -1L
        if (idMasterNumero != null && numero != null && numero != 0.toLong()) {
            val valorFinal = idMasterNumero / numero
            println("$idMasterNumero / $numero = $valorFinal")
            idMasterFinal = valorFinal
        } else {
            println("No se pudo calcular idMasterFinal: idMasterNumero=$idMasterNumero, numero=$numero")
        }


        val nombre = partes[1]
        val matricula = partes[2]
        val clase = partes[3]
        val grupo = partes[4]

        return if (grupo != grupoEsperado) {
            "❌ Este QR no pertenece al grupo"
        } else if (idMasterFinal == -1L or idMasterFinal) {
            "⚠️ Código QR anterior o modificado"
        } else {
            mandarAsistencia.add(idMasterFinal.toInt())
            "Alumno: $nombre\nMatrícula: $matricula\nClase: $clase\nGrupo: $grupo\n"
        }

    }

    LaunchedEffect(permissionGranted) {
        if (permissionGranted) {
            startCamera(
                context = context,
                lifecycleOwner = lifecycleOwner,
                previewView = previewView,
                cameraExecutor = cameraExecutor,
                barcodeScanner = barcodeScanner,
                onBarcodeDetected = { contenidoQR ->
                    scanRawResult = contenidoQR
                    val contrasenia = viewModel.contraseniaSemanal.value
                    val partes = contenidoQR.split(",").map { it.trim() }
                    scanValidResult = if (partes.size >= 5) {
                        if (contrasenia != null) {
                            procesarResultado(contenidoQR, grupo, contrasenia)
                        } else {
                            println("La contrasenia es: $contrasenia")
                            println("La contrasenia es nula")
                            "Esperando contrasenia válida..."
                        }
                    } else {
                        "Esperando QR válido..."
                    }
                }

            )
        }
    }

    //  var mostrarAlertaNoAceptado by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /*   if (mostrarAlertaNoAceptado) {
            AlertaAlumnoNoAceptado(onDismiss = { mostrarAlertaNoAceptado = false })
        }*/

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
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                }

                // Columna central: Título
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Registrar Asistencia",
                        style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Subtítulo
            // Subtítulo
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = materia,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge.copy(color = Color(0xFF2196F3))
                )
                Text(
                    text = "Grupo: $grupo",
                    fontSize = 14.sp
                )
                Text(
                    text = "Hora: $hora",
                    fontSize = 14.sp
                )
            }


            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "Escanea aquí", fontSize = 16.sp, fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Vista de la cámara
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, Color(0xFF2196F3), RoundedCornerShape(20.dp))
            ) {
                if (permissionGranted) {
                    AndroidView(
                        factory = { previewView },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Permiso no concedido", color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            // Botones (sin funcionalidad)
//        Row(
//            horizontalArrangement = Arrangement.SpaceEvenly,
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp)
//        ) {
//            IconButton(onClick = { /* TODO: Activar linterna */ }) {
//                Icon(Icons.Default.FlashOn, contentDescription = "Flash", tint = Color(0xFF2196F3))
//            }
//            IconButton(onClick = { /* Aquí podrías usar para simular captura */ }) {
//                Icon(Icons.Default.RadioButtonUnchecked, contentDescription = "Escanear", tint = Color(0xFF2196F3), modifier = Modifier.size(48.dp))
//            }
//            IconButton(onClick = { /* TODO: Cambiar cámara */ }) {
//                Icon(Icons.Default.Cached, contentDescription = "Cambiar", tint = Color(0xFF2196F3))
//            }
//        }

            Spacer(modifier = Modifier.height(20.dp))


            // Resultado escaneado
            OutlinedTextField(
                value = scanValidResult,
                onValueChange = {},
                label = { Text("Resultado del escaneo") },
                readOnly = true,
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(min = 60.dp, max = 200.dp)
            )

            val coroutineScope = rememberCoroutineScope()
            Spacer(modifier = Modifier.height(20.dp))
            SnackbarHost(
                hostState = snackbarHostState,
            )

            Button(
                onClick = {
                    if (mandarAsistencia.isNotEmpty()) {
                        viewModel.enviarAsistencias(mandarAsistencia.toList())
                        mandarAsistencia.clear()
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("✅ Lista de asistencia actualizada.")
                        }
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("⚠️ No hay asistencias nuevas que enviar.")
                        }
                    }
                },
                enabled = puedeAceptar,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (puedeAceptar) Color(0xFF4CAF50) else Color.Gray
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Aceptar",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }


        }
    }
}


private fun startCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraExecutor: Executor,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onBarcodeDetected: (String) -> Unit
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

    cameraProviderFuture.addListener({
        val cameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(cameraExecutor) { imageProxy ->
                    processImageProxy(
                        context = context,
                        imageProxy = imageProxy,
                        barcodeScanner = barcodeScanner,
                        onBarcodeDetected = onBarcodeDetected
                    )
                }
            }

        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalyzer
        )
    }, ContextCompat.getMainExecutor(context))
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImageProxy(
    context: Context,
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    onBarcodeDetected: (String) -> Unit
) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                for (barcode in barcodes) {
                    val barcodeText = barcode.url?.url ?: barcode.displayValue
                    onBarcodeDetected(barcodeText ?: "No se detectó código")
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al escanear código", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
    }
}
