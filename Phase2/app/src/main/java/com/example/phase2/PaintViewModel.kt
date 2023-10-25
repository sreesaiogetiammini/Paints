package com.example.phase2

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.lifecycle.ViewModel

class PaintViewModel: ViewModel(){

    val gravityOffset = mutableStateOf(Offset(0f, 0f))
    val trailPositions = mutableStateListOf<Offset>() // For storing the marble trail
    private val lines = mutableStateListOf<Line>()
    private val _lineColor = mutableStateOf(Color.Black)
    var lineColor = _lineColor

    private val _lineStroke = mutableStateOf(Stroke(10f , cap = StrokeCap.Round))
    val lineStroke = _lineStroke

    private val images = mutableStateListOf<ImageData>()
    private val texts = mutableStateListOf<TextBox>()
    fun addLine(line: Line) {
        lines.add(line)
    }

    fun clearLines() {
        lines.clear()
    }
    fun getLines(): List<Line> {
        return lines
    }

    fun addImage(image: ImageData) {
        images.add(image)
    }

    fun removeImage(image: ImageData) {
        images.remove(image)
    }

    fun getImages(): List<ImageData> {
        return images
    }


    fun addTexts(text: TextBox) {
        texts.add(text)
    }

    fun removeTexts(text: TextBox) {
        texts.remove(text)
    }

    fun clearTexts() {
        texts.clear()
    }
    fun getTexts(): List<TextBox> {
        return texts
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


    fun getLineColor() : Color {
       return lineColor.value
    }

    fun updateImageDataBySrc(src: Uri, updatedImageData: ImageData) {
        val imageIndex = images.indexOfFirst { it.src == src }
        if (imageIndex != -1) {
            // Image with the given src found, update it
            images[imageIndex] = updatedImageData
        }
    }

    fun updateTextCoordinates(text: TextBox, offsetX: Float, offsetY: Float) {
        text.x = offsetX
        text.y = offsetY

    }

    fun updateTextValue(text: TextBox, newValue:String) {
        text.value = newValue

    }

}

