package com.example.phase2

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope

class PaintsRepository(
    val scope: CoroutineScope,
    private val paintsDao: PaintsDao) {

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
}
