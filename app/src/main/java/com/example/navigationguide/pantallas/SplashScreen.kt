package com.example.navigationguide.pantallas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navigationguide.R
import com.example.navigationguide.core.navigation.Home
import com.example.navigationguide.core.navigation.Splash
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Asegura que el primer frame se haya dibujado
    LaunchedEffect(Unit) {
        withFrameNanos { }
        delay(1000L)
        navController.navigate(Home) {
            popUpTo(Splash) { inclusive = true } // elimina splash del stack
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF3466BB)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.logo_scanli_pequenio),
                contentDescription = "Logo",
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Scanli",
                fontSize = 60.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                color = Color.White
            )
        }
    }
}
