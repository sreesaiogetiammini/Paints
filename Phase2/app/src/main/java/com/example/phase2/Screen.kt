package com.example.phase2

sealed class Screen (val route: String){
    object SplashScreen: Screen("splash_screen")
    object LoginScreen: Screen("login_screen")
    object UserScreen: Screen("user_screen")
    object DrawScreen: Screen("draw_screen")
    object SignUpScreen: Screen("signup_screen")
}

