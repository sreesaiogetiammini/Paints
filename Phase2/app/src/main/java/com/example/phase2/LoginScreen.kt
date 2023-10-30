package com.example.phase2

import android.util.Log
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
fun LoginScreen(navController: NavController, databaseHelper: DatabaseHelper) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val isError by remember { derivedStateOf { snackbarMessage != null } }
    val errorColor: Color by animateColorAsState(if (isError) Color.Red else Color.Transparent)

    var user by remember { mutableStateOf(Firebase.auth.currentUser) }

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
                    .background(MaterialTheme.colorScheme.primaryContainer)  // Color.Purple
            )
            Spacer(modifier = Modifier.height(16.dp))  // Added spacer for some space between image and text
            Text(text = "Welcome to Paints")
                OutlinedTextField(value = email, onValueChange = { email = it.trim()}, label = { Text("Email") })
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it.trim() },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        snackbarMessage = "Email and Password cannot be empty."
                    }

                    Firebase.auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign-in was successful
                                 user = Firebase.auth.currentUser
                                 val userID = user!!.uid
                                 Log.e("ID",user.toString())
                                 snackbarMessage = "Excited for your new Masterpiece"
                                navController.navigate(Screen.UserScreen.route+"/$userID" )
                            }
                            else {
                                // Sign-in failed, handle the error.
                                 snackbarMessage = task.exception!!.message.toString()
                                // Handle the error, e.g., show an error message.
                            }
                        }


//                    else if (databaseHelper.checkUser(email, password)) {
//                        // Valid credentials, navigate to UserScreen
//                        val userID = databaseHelper.getUser(email, password)
//                        Log.e("ID",userID.toString())
//                        snackbarMessage = "Excited for your new Masterpiece"
//                        navController.navigate(Screen.UserScreen.route+"/$userID" )
//                    } else {
//                        // Handle invalid credentials
//                        snackbarMessage = "The email address or Password that you've entered doesn't match any account. Sign up for an account."
//                    }
                }) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    // Navigate to SignUpScreen for user registration
                    navController.navigate(Screen.SignUpScreen.route)
                }) {
                    Text("Signup")
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

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    val databaseHelper = DatabaseHelper(LocalContext.current)  // Note: This won't work in preview
    LoginScreen(navController = navController, databaseHelper = databaseHelper)
}
