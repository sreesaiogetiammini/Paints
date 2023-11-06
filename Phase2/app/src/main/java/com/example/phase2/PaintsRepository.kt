package com.example.phase2

import android.content.Context
import android.util.Log
import androidx.annotation.WorkerThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PaintsRepository(
    val scope: CoroutineScope,
    private val paintsDao: PaintsDao,
    context: Context
) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
//    val allPaintsData: LiveData<List<PaintsData>> = paintsDao.getAllPaintsData()

    @WorkerThread
    suspend fun insertPaintsData(data: PaintsData) {
        paintsDao.insertPaintsData(data)
    }

    @WorkerThread
    suspend fun getDrawingByDrawingName(drawingName: String, userId: String): PaintsData? {
        val paintsData = paintsDao.getDrawingByDrawingName(drawingName, userId)
        Log.e("data",paintsData.toString())
        return paintsData
    }

    @WorkerThread
    suspend fun updatePaintsData(paintsData: PaintsData) {
        paintsDao.updatePaintsData(paintsData)
    }

    @WorkerThread
    fun getPaintingsByUserId(userId: String): List<PaintsData> {
        return paintsDao.getPaintingsByUserId(userId)
    }

    @WorkerThread
    suspend fun setPaintingAsGlobal(userId: String, drawingName: String) {
//        return paintsDao.setDrawingAsGlobal(drawingName,userId)
        val existingData = paintsDao.getDrawingByDrawingName(drawingName, userId)
        if (existingData != null) {
            existingData.isGlobal = true
            Log.e("global/not", existingData.isGlobal.toString())
            paintsDao.updatePaintsData(existingData)
        }
    }

    @WorkerThread
    suspend fun getPaintingNamesByUserId(userId: String): List<String> {
        return withContext(Dispatchers.IO) {
            paintsDao.getDrawingNamesByUserId(userId)
        }
    }


    @WorkerThread
    suspend fun deletePaintingByDrawingName(drawingName: String, userId: String): Int {
        return withContext(Dispatchers.IO) {
            paintsDao.deletePaintingByDrawingName(drawingName,userId)
        }
    }
}
