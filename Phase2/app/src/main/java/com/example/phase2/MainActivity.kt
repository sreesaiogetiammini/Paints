package com.example.phase2

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.phase2.ui.theme.Phase2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Phase2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    Navigation(context = applicationContext);
                }
            }
        }
    }
}

@Composable
fun Navigation(context: Context) {
    val navController = rememberNavController()
//    val context = LocalContext.current
    val databaseHelper = DatabaseHelper(context)
    val application = PaintsApplication(context)

    NavHost(navController = navController, startDestination = Screen.SplashScreen.route ){
        // Our Team Logo should be here
        composable(route = Screen.SplashScreen.route){
            SplashScreen(navController)
        }
        // User Login , SignUp Should be here
        composable(route = Screen.LoginScreen.route){
            LoginScreen(navController = navController, databaseHelper = databaseHelper)
        }
        composable(route = Screen.SignUpScreen.route){
            SignUpScreen(navController = navController, databaseHelper = databaseHelper)
        }


        composable(
            route = Screen.UserScreen.route+"/{userId}"

        )
        {
            val userId = it.arguments?.getString("userId")
            if (userId != null) {
                UserScreen(navController = navController, paintsRepository = application.paintsRepository, userId)
            }
            else {
                //Toast.makeText("Sorry, we are facing trouble now. Please try again later.", Toast.LENGTH_SHORT).show()
            }

        }


        composable(route = Screen.DrawScreen.route +"/{userId}/{drawingName}"){
            var drawingName = it.arguments?.getString("drawingName")
            var id = it.arguments?.getString("userId")
            if (drawingName != null && drawingName != "dummy") {
                DrawScreen(navController = navController, paintsRepository = application.paintsRepository, drawingName = drawingName, userId = id.toString())
            } else {
                drawingName = "dummy"
                DrawScreen(navController = navController, paintsRepository = application.paintsRepository, drawingName = drawingName, userId = id.toString())
            }
        }

        // Add this if you have a separate SignUpScreen
        /*
        composable(route = Screen.SignUpScreen.route){
            SignUpScreen(navController = navController, databaseHelper = databaseHelper)
        }
        */
    }
}