package com.example.phase2

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.TestCase.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class MainActivityTest {

    var navController: NavHostController? = null
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testMainActivity() {

        composeTestRule.setContent {
            // Simulate launching the MainActivity
             navController = rememberNavController()
            MainActivity()
        }

        // Perform UI assertions for the MainActivity
        composeTestRule.onNodeWithText("Splash Page").assertIsDisplayed()
        // You can add more UI assertions as needed

        // Simulate a click action (e.g., navigating to LoginScreen)
        composeTestRule.onNodeWithText("Splash Page").performClick()

        // Perform navigation assertions
        composeTestRule.runOnIdle {

            assertTrue(navController!!.currentBackStackEntry?.destination?.route == Screen.LoginScreen.route)
        }
    }

    @Test
    fun testNavigation() {
        composeTestRule.setContent {
            // Simulate launching the Navigation Composable
            navController = rememberNavController()
            Navigation(context = InstrumentationRegistry.getInstrumentation().targetContext)
        }

        // Perform UI assertions for the Navigation Composable
        composeTestRule.onNodeWithText("Splash Page").assertIsDisplayed()
        // You can add more UI assertions as needed

        // Simulate navigation (e.g., navigating to LoginScreen)
        composeTestRule.onNodeWithText("Splash Page").performClick()

        // Perform navigation assertions
        composeTestRule.runOnIdle {
            assertTrue(navController!!.currentBackStackEntry?.destination?.route == Screen.LoginScreen.route)
        }
    }
}
