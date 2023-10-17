package com.example.phase2


import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test



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

    private lateinit var viewModel: PaintViewModel


    @Before
    fun setup() {
        viewModel = PaintViewModel()
    }

    @Test
    fun addLine_increasesLineCount() {
        val initialLineCount = viewModel.getLines().size
        val line = Line(
            start = Offset(0f, 0f),
            end = Offset(100f, 100f),
            color = Color.Black
        )
        viewModel.addLine(line)
        val updatedLineCount = viewModel.getLines().size
        assertEquals(initialLineCount + 1, updatedLineCount)
    }

    @Test
    fun clearLines_removesAllLines() {
        val line = Line(
            start = Offset(0f, 0f),
            end = Offset(100f, 100f),
            color = Color.Black
        )
        viewModel.addLine(line)
        viewModel.clearLines()
        val lineCount = viewModel.getLines().size
        assertEquals(0, lineCount)
    }

    @Test
    fun updateLineColor_changesLineColor() {
        viewModel.updateLineColor(Color.Blue)
        assertEquals(Color.Blue, viewModel.lineColor.value)
    }

    @Test
    fun updateLineStroke_changesLineStroke() {
        val newStroke = Stroke(5f)
        viewModel.updateLineStroke(newStroke)
        assertEquals(newStroke, viewModel.lineStroke.value)
    }

    @Test
    fun updatePathStrokeCap_changesStrokeCap() {
        viewModel.updateLineStrokeCap(StrokeCap.Square)
        assertEquals(StrokeCap.Square, viewModel.lineStroke.value.cap)
    }

    @Test
    fun updatePathStrokeWidth_changesStrokeWidth() {
        val newStrokeWidth = 8f
        viewModel.updateLineStrokeWidth(newStrokeWidth)
        assertEquals(newStrokeWidth, viewModel.lineStroke.value.width)
    }


//    @get:Rule
//    val composeTestRule: ComposeTest
}

