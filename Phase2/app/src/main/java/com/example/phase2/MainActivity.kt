package com.example.phase2

import ImageDataTypeAdapter
import android.content.Context
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.phase2.ui.theme.Phase2Theme
import com.google.gson.Gson
import com.google.gson.GsonBuilder

class MainActivity : ComponentActivity() {

    private lateinit var sensorManager: SensorManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager


        // val gson: Gson = GsonBuilder()
        //            .registerTypeAdapter(ImageData::class.java, ImageDataSerializer())
        //            .create()
        setContent {
            Phase2Theme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                )
                {
                    Navigation(context = applicationContext,sensorManager);
                }
            }
        }
    }
}

@Composable
fun Navigation(context: Context,sensorManager: SensorManager) {
    val navController = rememberNavController()
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
                DrawScreen(navController = navController, paintsRepository = application.paintsRepository, drawingName = drawingName, userId = id.toString(),
                    sensorManager = sensorManager
                )
            } else {
                drawingName = "dummy"
                DrawScreen(navController = navController, paintsRepository = application.paintsRepository, drawingName = drawingName, userId = id.toString(),
                    sensorManager = sensorManager
                )
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