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
fun SignUpScreen(navController: NavController, databaseHelper: DatabaseHelper) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Sign Up Page")
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") })
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = { Text("Confirm Password") })
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                if(password == confirmPassword) {
                    if (databaseHelper.insertData(email, password)) {
                        // Successfully registered
                        navController.navigate("YourLoginRouteHere")
                    } else {
                        // Handle registration failure
                        snackbarMessage = "Registration failed. Email might be taken."
                    }
                } else {
                    // Handle password mismatch
                    snackbarMessage = "Password and confirmation do not match."
                }
            }) {
                Text("Sign Up")
            }
        }

        // Display the Snackbar when there's a message
        snackbarMessage?.let { message ->
            Snackbar(
                modifier = Modifier.align(Alignment.BottomCenter).padding(8.dp),
                action = {
                    TextButton(onClick = { snackbarMessage = null }) {
                        Text("OK")
                    }
                }
            ) {
                Text(text = message)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    val databaseHelper = DatabaseHelper(LocalContext.current) // Note: This won't work in preview
    SignUpScreen(navController = navController, databaseHelper = databaseHelper)
}
