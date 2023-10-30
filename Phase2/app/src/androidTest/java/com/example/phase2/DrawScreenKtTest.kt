package com.example.phase2

import androidx.compose.runtime.Composable
import androidx.compose.ui.semantics.SemanticsProperties.ContentDescription
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test

class DrawScreenKtTest{
    var navController: NavHostController? = null
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val databaseHelper = DatabaseHelper(context)
    val application = PaintsApplication(context)


    @get:Rule
    val composeTestRule = createComposeRule()

    @Composable
    fun TestDrawScreen() {
        navController = rememberNavController()
        NavHost(navController = navController!!, startDestination = Screen.DrawScreen.route) {
            composable(route = Screen.DrawScreen.route) {
                //DrawScreen(navController = navController!!, paintsRepository = application.paintsRepository , userId = "1", drawingName = "dummy")
            }

            composable(route = Screen.UserScreen.route +"/{userId}") {
                UserScreen(navController = navController!!, paintsRepository = application.paintsRepository,"1"
                )
            }
        }
    }

    @Test
    fun testDrawScreenUIElements(){
        composeTestRule.setContent {
            TestDrawScreen()
        }
        composeTestRule
            .onAllNodesWithContentDescription("AnimatedIconButtons").assertCountEquals(6)
        composeTestRule.onNodeWithContentDescription("Account Home").assertIsDisplayed()
        // Verify the initial state of the UI components
        composeTestRule.onNodeWithContentDescription("Account Home").performClick()
        assert(navController!!.currentDestination?.route == Screen.UserScreen.route +"/{userId}")

    }

    @Test
    fun testAnimatedIconButton() {
        composeTestRule.setContent {
            TestDrawScreen()
        }
        val filter = SemanticsMatcher.expectValue(ContentDescription, listOf("AnimatedIconButtons"))
        val nodes = composeTestRule.onAllNodes(matcher = filter)
        nodes[0].performClick()
        composeTestRule.onNodeWithContentDescription("Width Slider").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("P.a.i.n.t.s image").performClick()
        nodes[1].performClick()
        composeTestRule.onNodeWithContentDescription("Butt Cap").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Square Cap").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Round Cap").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("P.a.i.n.t.s image").performClick()
    }


}

