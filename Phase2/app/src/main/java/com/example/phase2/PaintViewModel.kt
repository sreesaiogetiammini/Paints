package com.example.phase2

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
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

    fun updateImageCoordinates( imagedata: ImageData, offsetX: Float, offsetY: Float) {
        imagedata.x = offsetX
        imagedata.y = offsetY
    }


    fun updateTextCoordinates(text: TextBox, offsetX: Float, offsetY: Float) {
        text.x = offsetX
        text.y = offsetY

    }

    fun updateTextValue(text: TextBox, newValue:String) {
        text.value = newValue

    }


    fun captureCanvasAsBitmap(canvasWidth: Int,canvasHeight:Int,context:Context): Bitmap {

        println("No of Images "+images.size)
        // Create a blank Bitmap with the specified dimensions
        val canvasBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)

        // Create a Canvas to draw on the Bitmap
        val canvas = Canvas(canvasBitmap)

        // Clear the canvas with a white background
        canvas.drawColor(Color.White.toArgb())

        // Draw lines, images, and texts
        for (line in lines) {
            val paint = Paint()
            paint.color = line.color.toArgb()
            paint.strokeWidth = line.strokeWidth.width
            canvas.drawLine(line.start.x, line.start.y, line.end.x, line.end.y, paint)
        }
        for (image in images) {
            // Draw images as needed
            val imageUri = image.src
            val imageX = image.x
            val imageY = image.y

            val desiredWidth = 200
            val desiredHeight = 200

            try {
                val imageStream = context.contentResolver.openInputStream(imageUri)
                val originalBitmap = BitmapFactory.decodeStream(imageStream)

                // Scale the original bitmap to the desired width and height
                val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, desiredWidth, desiredHeight, false)

                canvas.drawBitmap(scaledBitmap, imageX, imageY, null)
            }
            catch (e: Exception) {
                Log.e("Image Draw Exception",e.toString())
            }
        }
        for (text in texts) {
            // Draw text from TextBox objects
            val textPaint = Paint()
            textPaint.color = Color.Black.toArgb() // Set text color
            textPaint.textSize = 20f // Set text size

            // Draw text on the canvas at the specified x and y coordinates
            canvas.drawText(text.value, text.x, text.y, textPaint)
        }

        return canvasBitmap
    }





}

