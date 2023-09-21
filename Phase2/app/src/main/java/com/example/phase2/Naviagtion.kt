package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.SpalshScreen.route ){
        composable(route = Screen.SpalshScreen.route){
           SplashScreen(navController = navController)
        }
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