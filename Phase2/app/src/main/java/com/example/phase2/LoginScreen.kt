package com.example.phase2

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginSignupScreen(navController: NavController, databaseHelper: DatabaseHelper) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Welcome to Come Back")
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            if (databaseHelper.checkUser(email, password)) {
                navController.navigate(Screen.UserScreen.route)
            } else {
                // Handle invalid credentials
            }
        }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {
            if (databaseHelper.insertData(email, password)) {
                // Successfully registered
            } else {
                // Handle registration failure
            }
        }) {
            Text("Signup")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginSignupScreenPreview() {
    val navController = rememberNavController()
    val databaseHelper = DatabaseHelper(LocalContext.current) // Note: This won't work in preview
    LoginSignupScreen(navController = navController, databaseHelper = databaseHelper)
}
