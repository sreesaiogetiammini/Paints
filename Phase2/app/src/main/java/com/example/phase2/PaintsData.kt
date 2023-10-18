package com.example.phase2

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paints")
data class PaintsData(
    var drawingName: String,
    var drawingData: String,
    var userId: Long,
//    var misc: String, // Store image sources and coordinates
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 // Primary key for the DB
}