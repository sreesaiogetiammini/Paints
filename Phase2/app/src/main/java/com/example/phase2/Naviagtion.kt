package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SpalshScreen.route ){
        // Our Team Logo should be here
        composable(route = Screen.SpalshScreen.route){
           SplashScreen(navController = navController)
        }
        // User Login , SignUp Should be here
        composable(route = Screen.LoginScreen.route){
            LoginSignupScreen(navController = navController)
        }
        composable(route = Screen.UserScreen.route){
            UserScreen(navController = navController)
        }
        composable(route = Screen.DrawScreen.route){
           DrawScreen(navController = navController)
        }
    }
}