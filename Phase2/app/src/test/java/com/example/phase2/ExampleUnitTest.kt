package com.example.phase2

import androidx.navigation.NavController
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    private lateinit var drawScreen: Screen.DrawScreen

    @Mock
    private lateinit var navController: NavController

    @Mock
    private lateinit var paintsRepository: PaintsRepository

    private val drawingName = "MyDrawing"

    @Before
    fun setup() {

        MockitoAnnotations.initMocks(this)

        drawScreen = Screen.DrawScreen
    }
}