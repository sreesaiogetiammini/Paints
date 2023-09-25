package com.example.phase1

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.random.Random
@RequiresApi(Build.VERSION_CODES.O)
class PaintViewModel: ViewModel(){


    private val drawingPathsLiveData = MutableLiveData<List<Pair<Path, Paint>>>()

    fun setDrawingPaths(paths: List<Pair<Path, Paint>>) {
        drawingPathsLiveData.value = paths
    }

    fun getDrawingPaths(): LiveData<List<Pair<Path, Paint>>> {
        return drawingPathsLiveData
    }


}
