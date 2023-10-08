package com.example.phase2



import android.graphics.Paint
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel

class PaintViewModel: ViewModel(){



    private val lines = mutableStateListOf<Line>()
    private val _lineColor = mutableStateOf(Color.Black)
    var lineColor = _lineColor

    private val _lineStroke = mutableStateOf(Stroke(10f))
    val lineStroke = _lineStroke

    fun addLine(line: Line) {
        lines.add(line)
    }

    fun clearLines() {
        lines.clear()
    }
    fun getLines(): List<Line> {
        return lines
    }


    // Functions to update line color and stroke
    fun updateLineColor(newColor: Color) {
        _lineColor.value = newColor
    }

    fun updateLineStroke(newStroke: Stroke) {
        _lineStroke.value = newStroke
    }
}


