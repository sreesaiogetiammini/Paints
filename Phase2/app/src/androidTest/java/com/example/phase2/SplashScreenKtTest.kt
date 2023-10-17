package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.junit.Rule
import org.junit.Test


class SplashScreenKtTest{
    var navController: NavHostController? = null
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    fun TestSplashScreen() {
         navController = rememberNavController()
        NavHost(navController = navController!!, startDestination = Screen.SplashScreen.route) {
            composable(route = Screen.SplashScreen.route) {
                SplashScreen(navController!!)
            }
            composable(route = Screen.LoginScreen.route) {

            }
        }
    }

    @Test
    fun testSplashScreen() {
        composeTestRule.setContent {
                TestSplashScreen()
            }

        // Verify the initial state of the UI components
        composeTestRule.onNodeWithText("Splash Page").assertIsDisplayed()
        composeTestRule.onNodeWithText("How you doin'?").assertIsDisplayed()
        composeTestRule.onNodeWithText("Splash Page").performClick()
        assert(navController!!.currentDestination?.route == Screen.LoginScreen.route)
    }





}





