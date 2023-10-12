package com.example.phase2

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel

class PaintViewModel: ViewModel(){


    private val _pathColor = mutableStateOf(Color.Black)
    val pathColor = _pathColor



    private val lines = mutableStateListOf<Line>()
    private val _lineColor = mutableStateOf(Color.Black)
    var lineColor = _lineColor

    private val _lineStroke = mutableStateOf(Stroke(10f , cap = StrokeCap.Round))
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

    fun updateLineStrokeCap(strokeCap: StrokeCap) {
        val updatedStroke = Stroke(cap = strokeCap, width = lineStroke.value.width)
        lineStroke.value = updatedStroke
    }

    fun updateLineStrokeWidth(strokeWidth:  Float) {
        val updatedStroke = Stroke(width = strokeWidth,cap = lineStroke.value.cap)
        lineStroke.value = updatedStroke
    }

}

