package com.example.phase2


//Only allows classes inside of this class to inherit from screen
sealed class Screen(val route: String){
    object SpalshScreen: Screen("splash_screen")
    object LoginScreen: Screen("login_screen")
    object UserScreen: Screen("user_screen")
    object DrawScreen: Screen("draw_screen")
}

