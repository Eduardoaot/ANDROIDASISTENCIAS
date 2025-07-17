package com.example.navigationguide.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.navigationguide.DataEScreen
import com.example.navigationguide.ErrorScreen
import com.example.navigationguide.HomeScreen
import com.example.navigationguide.ListaScreen
import com.example.navigationguide.ScanningScreen
import com.example.navigationguide.SplashScreen

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()

    NavHost(navController= navController, startDestination = Splash){
        composable<Splash> {
            SplashScreen(navController)
        }
        composable <Home>{
            HomeScreen(navController)
        }
        composable<Scanner>
        { backStackEntry ->
            val scanner: Scanner = backStackEntry.toRoute()
            ScanningScreen(
                navController = navController,
                materia = scanner.materia,
                grupo = scanner.grupo,
                hora = scanner.hora
            )
        }
        composable<Data> {
            DataEScreen(navController = navController)
        }
        composable<ListaAsistencia> { backStackEntry ->
            val ListaAsistencia: ListaAsistencia = backStackEntry.toRoute()
            ListaScreen(
                navController = navController,
                idGrupo = ListaAsistencia.idGrupo,
            )
        }
        composable<Error> {
            ErrorScreen()
        }
    }
}

