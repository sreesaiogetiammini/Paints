package com.example.phase2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginSignupScreen(navController:NavController) {

    Column (
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Text(text = "Login Page")
        Button(onClick = { navController.navigate(Screen.UserScreen.route) }) {
            Text("Login")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun LoginSignupScreenPreview() {
    val navController = rememberNavController() // Create a dummy NavController
    LoginSignupScreen(navController = navController)
}