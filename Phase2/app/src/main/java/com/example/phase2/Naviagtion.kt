package com.example.phase2

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)

    NavHost(navController = navController, startDestination = Screen.SpalshScreen.route ){
        // Our Team Logo should be here
        composable(route = Screen.SpalshScreen.route){
            SplashScreen(navController = navController)
        }
        // User Login , SignUp Should be here
        composable(route = Screen.LoginScreen.route){
            LoginSignupScreen(navController = navController, databaseHelper = databaseHelper)
        }
        composable(route = Screen.UserScreen.route){
            UserScreen(navController = navController)
        }
        composable(route = Screen.DrawScreen.route){
            DrawScreen(navController = navController, context = context)
        }

        // Add this if you have a separate SignUpScreen
        /*
        composable(route = Screen.SignUpScreen.route){
            SignUpScreen(navController = navController, databaseHelper = databaseHelper)
        }
        */
    }
}
