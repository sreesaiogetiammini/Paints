package com.example.phase2

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
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
    suspend fun getDrawingByDrawingName(drawingName: String, userId: Long): PaintsData? {
        return paintsDao.getDrawingByDrawingName(drawingName, userId)
    }

    @WorkerThread
    suspend fun updatePaintsData(paintsData: PaintsData) {
        paintsDao.updatePaintsData(paintsData)
    }

    @WorkerThread
    fun getPaintingsByUserId(userId: Long): List<PaintsData> {
        return paintsDao.getPaintingsByUserId(userId)
    }

    @WorkerThread
    suspend fun getPaintingNamesByUserId(userId: Long): List<String> {
        return withContext(Dispatchers.IO) {
            paintsDao.getDrawingNamesByUserId(userId)
        }
    }
}
