package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.espresso.Espresso.pressBack
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenKtTest{

    var navController: NavHostController? = null
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val mockDatabaseHelper = DatabaseHelper(context)
    @get:Rule
    val composeTestRule = createComposeRule()


    @Composable
    fun TestLoginScreen() {

        navController = rememberNavController()
        NavHost(navController = navController!!, startDestination = Screen.LoginScreen.route) {
            composable(route = Screen.LoginScreen.route) {
                LoginScreen(navController!!,mockDatabaseHelper!!)
            }
            composable(route = Screen.SignUpScreen.route) {

            }

        }
    }



    @Test
    fun testLoginPageUIElements(){

        composeTestRule.setContent {
            TestLoginScreen()
        }
        composeTestRule.onNodeWithText("Welcome to Paints").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        val emailTextField = composeTestRule.onNodeWithText("Email")
        emailTextField.performTextInput("test@gmail.com")
        composeTestRule.onNodeWithText("test@gmail.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        val passwordTextField = composeTestRule.onNodeWithText("Password")
        passwordTextField.performTextInput("1245623")
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        composeTestRule.onNodeWithText("Signup").assertIsDisplayed()
        composeTestRule.onNodeWithText("Signup").performClick()
        assert(navController!!.currentDestination?.route == Screen.SignUpScreen.route)

        pressBack()
        assert(navController!!.currentDestination?.route == Screen.LoginScreen.route)

    }


}