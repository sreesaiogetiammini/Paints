package com.example.phase2

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel

class PaintViewModel: ViewModel(){

    private val paths = mutableStateListOf<MyPath>()

    private val _pathColor = mutableStateOf(Color.Black)
    val pathColor = _pathColor

    private val _pathStroke = mutableStateOf(Stroke(10f))
    val pathStroke = _pathStroke

    private val _text = mutableStateOf("Your Text Here")
    val text: State<String> = _text

    private val _textPosition = mutableStateOf(Offset(100f, 100f))
    val textPosition: State<Offset> = _textPosition

    fun addPath(path: MyPath) {
        paths.add(path)
    }

    fun clearPaths() {
        paths.clear()
    }
    fun getPaths(): List<MyPath> {
        return paths
    }


    // Functions to update line color and stroke
    fun updatePathColor(newColor: Color) {
        _pathColor.value = newColor
    }

    fun updatePathStrokeCap(strokeCap: StrokeCap) {
        val updatedStroke = Stroke(cap = strokeCap, width = _pathStroke.value.width)
        _pathStroke.value = updatedStroke
    }


    fun updatePathStrokeWidth(strokeWidth:  Float) {
        val updatedStroke = Stroke(width = strokeWidth,cap = _pathStroke.value.cap)
        _pathStroke.value = updatedStroke
    }
    // Function to update the text
    fun updateText(newText: String) {
        _text.value = newText
    }

    // Function to update the text position
    fun updateTextPosition(newPosition: Offset) {
        _textPosition.value = newPosition
    }
}


