package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()
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

            UserScreen(navController = navController, paintsRepository = application.paintsRepository)
        }
        composable(route = Screen.DrawScreen.route + "/{drawingName}"){
            var drawingName = it.arguments?.getString("drawingName")
            if (drawingName != null && drawingName != "dummy") {
                DrawScreen(navController = navController, paintsRepository = application.paintsRepository, drawingName)
            } else {
                drawingName = "dummy"
                DrawScreen(navController = navController, paintsRepository = application.paintsRepository, drawingName)
            }
        }

        // Add this if you have a separate SignUpScreen
        /*
        composable(route = Screen.SignUpScreen.route){
            SignUpScreen(navController = navController, databaseHelper = databaseHelper)
        }
        */
    }
}
