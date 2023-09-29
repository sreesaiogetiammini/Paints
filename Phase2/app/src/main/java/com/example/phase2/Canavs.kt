//package com.example.phase2
//
//import androidx.compose.foundation.Canvas
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.Paint
//import androidx.compose.ui.graphics.Path
//import androidx.compose.ui.graphics.PathEffect
//import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.graphics.asImageBitmap
//import androidx.compose.ui.graphics.drawscope.Stroke
//import androidx.compose.ui.graphics.toArgb
//import androidx.compose.ui.res.imageResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.IntSize
//import androidx.compose.ui.viewinterop.AndroidView
//import kotlin.math.cos
//import kotlin.math.sin
//
//@Composable
//fun CanvasWithTextAndImage() {
//    val text = "Hello, Canvas!"
//    val textSize = 32.sp
//    val textColor = Color.Black
//
//    val imageBitmap = R.drawable.ic_launcher_foreground
//
//    Canvas(
//        modifier = Modifier.fillMaxSize(),
//        onDraw = { canvas ->
//            // Draw a background color
//            canvas.drawColor(Color.White)
//
//            // Draw text
//            val textPaint = Paint().asFrameworkPaint()
//            textPaint.textSize = textSize.toPx()
//            textPaint.color = textColor.toArgb()
//            canvas.nativeCanvas.drawText(text, 50f, 100f, textPaint)
//
//            // Draw an image
//            val imageLeft = 50f
//            val imageTop = 150f
//            canvas.nativeCanvas.drawBitmap(imageBitmap.asImageBitmap().asAndroidBitmap(), imageLeft, imageTop, null)
//        }
//    )
//}
//
//
//
//}
//
//@Preview
//@Composable
//fun CanvasWithTextAndImagePreview() {
//    CanvasWithTextAndImage()
//}
