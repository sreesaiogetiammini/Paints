package com.example.phase2

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke

data class MyPath(
    val path: Path = Path(),
    val color: Color, // Color for drawing the path
    val stroke: Stroke // Width of the stroke for the path

)
