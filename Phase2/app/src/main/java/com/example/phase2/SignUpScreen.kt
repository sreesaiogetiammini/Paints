package com.example.phase2

import android.util.Patterns
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun SignUpScreen(navController: NavController, databaseHelper: DatabaseHelper) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val isError by remember { derivedStateOf { snackbarMessage != null } }
    val errorColor: Color by animateColorAsState(if (isError) Color.Red else Color.Transparent)

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
                .padding(top = 0.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.paints),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
            )
            Text(text = "Sign Up Page")
            OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                when {
                    email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                        snackbarMessage = "All fields are required."
                    }
                    !isValidEmail(email) -> {
                        snackbarMessage = "Invalid email address."
                    }
                    password.length < 6 -> {
                        snackbarMessage = "Password must be at least 6 characters."
                    }
                    password != confirmPassword -> {
                        snackbarMessage = "Passwords do not match."
                    }
                    else -> {
//                        if (databaseHelper.insertData(email, password)) {
//                            // Successfully registered, navigate back to LoginScreen
//                            navController.navigate(Screen.LoginScreen.route) {
//                                popUpTo(Screen.LoginScreen.route) { inclusive = true }
//                            }
//                        } else {
//                            // Handle registration failure
//                            snackbarMessage = "Registration failed. Email might be taken."
//                        }
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Registration was successful
                                    val user = Firebase.auth.currentUser
                                    databaseHelper.insertData(user!!.uid,email, password)
                                    snackbarMessage = "Sign Up Sucessful "
                                    navController.navigate(Screen.LoginScreen.route) {
                                        popUpTo(Screen.LoginScreen.route) { inclusive = true }
                                     }
                                    // Handle the newly registered user, e.g., navigate to the next screen.
                                } else {
                                    // Registration failed, handle the error.
                                    snackbarMessage = task.exception.toString()
                                    // Handle the error, e.g., show an error message.
                                }
                            }
                    }
                }
            }) {
                Text("Sign Up")
            }

            if (isError) {
                Text(
                    text = snackbarMessage ?: "",
                    fontWeight = FontWeight.Bold,
                    color = errorColor,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp)
                )
            }
        }
    }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    val navController = rememberNavController()
    val databaseHelper = DatabaseHelper(LocalContext.current) // Note: This won't work in preview
    SignUpScreen(navController = navController, databaseHelper = databaseHelper)
}