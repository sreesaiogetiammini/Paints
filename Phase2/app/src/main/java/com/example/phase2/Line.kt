package com.example.phase2

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke

data class Line(
    val start: Offset,
    val end: Offset,
    val color: Color = Color.Black,
    val strokeWidth: Stroke = Stroke(10f),
)

data class TextBox(
    var x: Float,
    var y: Float,
    var value:String,
)