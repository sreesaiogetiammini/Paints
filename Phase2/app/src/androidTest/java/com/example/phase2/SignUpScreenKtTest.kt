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
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpScreenKtTest{

    var navController: NavHostController? = null
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val mockDatabaseHelper = DatabaseHelper(context)
    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    fun TestSignUpScreen() {

        navController = rememberNavController()
        NavHost(navController = navController!!, startDestination = Screen.SignUpScreen.route) {

            composable(route = Screen.SignUpScreen.route) {
                    SignUpScreen(navController!!, mockDatabaseHelper)
            }
            composable(route = Screen.LoginScreen.route) {
                LoginScreen(navController!!, mockDatabaseHelper)
            }
            composable(  route = Screen.UserScreen.route+"/{userId}") {

            }
        }
    }



    @Test
    fun testSignUpUIElements(){

        composeTestRule.setContent {
            TestSignUpScreen()
        }
        composeTestRule.onNodeWithText("Sign Up Page").assertIsDisplayed()
        composeTestRule.onNodeWithText("Email").assertIsDisplayed()
        val emailTextField = composeTestRule.onNodeWithText("Email")
        emailTextField.performTextInput("test@gmail.com")
        composeTestRule.onNodeWithText("test@gmail.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Password").assertIsDisplayed()
        val passwordTextField = composeTestRule.onNodeWithText("Password")
        passwordTextField.performTextInput("1245623")

        composeTestRule.onNodeWithText("Confirm Password").assertIsDisplayed()
        val confirmPasswordTextField = composeTestRule.onNodeWithText("Confirm Password")
        confirmPasswordTextField.performTextInput("1245623")
        composeTestRule.onNodeWithText("Sign Up").assertIsDisplayed()
        composeTestRule.onNodeWithText("Sign Up").performClick()
        assert(navController!!.currentDestination?.route == Screen.LoginScreen.route)
        composeTestRule.onNodeWithText("Login").assertIsDisplayed()
        val loginEmailTextField = composeTestRule.onNodeWithText("Email")
        loginEmailTextField.performTextInput("test@gmail.com")
        val loginPasswordTextField = composeTestRule.onNodeWithText("Password")
        loginPasswordTextField.performTextInput("1245623")
        composeTestRule.onNodeWithText("Login").performClick()
        assert(navController!!.currentDestination?.route == Screen.UserScreen.route+"/{userId}")
    }


}