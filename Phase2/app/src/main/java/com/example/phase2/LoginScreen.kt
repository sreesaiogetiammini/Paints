package com.example.phase2

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.util.Log
import androidx.activity.result.IntentSenderRequest
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val isError by remember { derivedStateOf { snackbarMessage != null } }
    val errorColor: Color by animateColorAsState(if (isError) Color.Red else Color.Transparent, label = "errorColorAnimation")

    val firebaseAuth = Firebase.auth
    val activity = LocalContext.current as Activity // Casting Context to Activity
    val coroutineScope = rememberCoroutineScope()
    val googleAuthUiClient = GoogleAuthUiClient(activity)

    val activityResult = rememberUpdatedState<(ActivityResult) -> Unit> { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coroutineScope.launch {
                val signInResult = googleAuthUiClient.signInWithIntent(result.data!!)
                if (signInResult.data != null) {
                    // Successful Google Sign-In
                    // Navigate or do something with the result
                } else {
                    // Handle Google Sign-In failure
                    snackbarMessage = signInResult.errorMessage ?: "Google Sign-In failed"
                }
            }
        } else {
            snackbarMessage = "Google Sign-In was cancelled"
        }
    }

    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartIntentSenderForResult()) { result ->
        activityResult.value.invoke(result)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
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
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Welcome to Paints")
            OutlinedTextField(value = email, onValueChange = { email = it.trim() }, label = { Text("Email") })
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
                } else {
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = firebaseAuth.currentUser
                                val userID = user!!.uid
                                snackbarMessage = "Excited for your new Masterpiece"
                                navController.navigate(Screen.UserScreen.route + "/$userID")
                            } else {
                                snackbarMessage = task.exception!!.message.toString()
                            }
                        }
                }
            }) {
                Text("Login")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                Log.d("LoginScreen", "Google login button clicked")
                coroutineScope.launch {
                    val intentSender = googleAuthUiClient.signIn()
                    intentSender?.let { launcher.launch(IntentSenderRequest.Builder(it).build()) }
                }
            }) {
                Text("Login with Google")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
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
    LoginScreen(navController = navController)
}
