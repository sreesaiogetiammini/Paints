package com.example.phase2

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.*


@Entity(tableName = "paints")
@Serializable
data class PaintsData(
    var drawingName: String,
    var drawingData: String,
    var drawingImages: String,
    var drawingTexts: String,
    var userId: String,
    var isGlobal: Boolean = true
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0 // Primary key for the DB
}