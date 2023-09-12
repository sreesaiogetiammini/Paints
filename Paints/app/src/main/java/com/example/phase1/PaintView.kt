package com.example.phase1

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat


@RequiresApi(Build.VERSION_CODES.O)
class PaintView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var bitmap = Bitmap.createBitmap(800, 800, Bitmap.Config.ARGB_8888)
    private var bitmapCanvas = Canvas(bitmap)
    private val rect: Rect by lazy { Rect(0, 0, width, height) }
    private var paintBrush = Paint()
    private var paintColor = Color.BLACK
    private var currentStrokeWidth = 10f // Default stroke width
    private var paths = mutableListOf<Pair<Path, Paint>>() // List of paths with their respective stroke widths

    init {
        paintBrush.isAntiAlias = true
        paintBrush.color = paintColor
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.strokeWidth = currentStrokeWidth
    }

    fun clearPaths() {
        paths.clear()
        bitmap.eraseColor(Color.TRANSPARENT) // Clear the bitmap by making it transparent
        invalidate() // Refresh the view to clear the drawing
    }

    fun changeStrokeWidth(stroke: Float) {
        val newPaint = Paint(paintBrush)
        newPaint.color= paintColor
        newPaint.strokeWidth = stroke
        paintBrush = newPaint
        currentStrokeWidth = paintBrush.strokeWidth
        // Update strokeWidth for existing paths
    }

    fun changeColor(colors: Int) {
        val newPaint = Paint(paintBrush)
        newPaint.color =  colors
        newPaint.strokeWidth = currentStrokeWidth
        paintBrush = newPaint
        paintColor = paintBrush.color
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawBitmap(bitmap, null, rect, paintBrush)
        // Draw all paths with their respective Paint settings
        for ((path, paint) in paths) {
            canvas?.drawPath(path, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val newPath = Path()
                newPath.moveTo(x, y)
                val newPaint = Paint(paintBrush)
                paths.add(Pair(newPath, newPaint))
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                paths.lastOrNull()?.first?.lineTo(x, y)
            }
            else -> return false
        }
        postInvalidate()
        return true
    }

    fun setDrawingPath(path: Path, brush: Paint) {
        paths.add(Pair(path, brush))
        invalidate() // Redraw the view
    }

    fun getDrawingPaths(): List<Pair<Path, Paint>> {
        return paths
    }

}

