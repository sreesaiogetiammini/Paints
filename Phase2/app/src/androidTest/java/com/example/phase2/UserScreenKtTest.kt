package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class UserScreenKtTest{
    var navController: NavHostController? = null
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val databaseHelper = DatabaseHelper(context)
    val application = PaintsApplication(context)


    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    fun TestUserScreen() {
        navController = rememberNavController()
        NavHost(navController = navController!!, startDestination = Screen.UserScreen.route) {
            composable(route = Screen.UserScreen.route) {
                UserScreen(navController = navController!!, paintsRepository = application.paintsRepository,"1"
                )
            }
            composable(route = Screen.DrawScreen.route +"/{userId}/{drawingName}") {
                DrawScreen(navController = navController!!, paintsRepository = application.paintsRepository , userId = "1", drawingName = "dummy")
            }
        }
    }

    @Test
    fun testUserUIElements(){
        composeTestRule.setContent {
            TestUserScreen()
        }
        composeTestRule.onNodeWithContentDescription("Floating action button.").assertIsDisplayed()
        // Verify the initial state of the UI components
        composeTestRule.onNodeWithContentDescription("Floating action button.").performClick()

        assert(navController!!.currentDestination?.route == Screen.DrawScreen.route +"/{userId}/{drawingName}")
    }
}