package com.example.phase2 // Replace with your actual package name

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "paints") // Change the table name as needed
data class PaintsData(
    var drawingName: String,
    var drawingData: String,
    var userId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 // Primary key for the DB
}