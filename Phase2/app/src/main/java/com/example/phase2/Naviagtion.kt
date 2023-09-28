package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)

    NavHost(navController = navController, startDestination = Screen.SpalshScreen.route ){
        composable(route = Screen.SpalshScreen.route){
            SplashScreen(navController = navController)
        }
        composable(route = Screen.LoginScreen.route){
            LoginSignupScreen(navController = navController, databaseHelper = databaseHelper)
        }
        composable(route = Screen.UserScreen.route){
            UserScreen(navController = navController)
        }
        composable(route = Screen.DrawScreen.route){
            DrawScreen(navController = navController)
        }

        // Add this if you have a separate SignUpScreen
        /*
        composable(route = Screen.SignUpScreen.route){
            SignUpScreen(navController = navController, databaseHelper = databaseHelper)
        }
        */
    }
}
