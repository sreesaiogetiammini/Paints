package com.example

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object User : IntIdTable() {} //not storing anything else here...

object Painting: IntIdTable(){
    val drawingName = text("drawingName")
    val drawingData = text("drawingData")
    val drawingImages = text("drawingImages")
    val drawingTexts = text("drawingTexts")
    val userId = text("userId")
    val isGlobal = bool("isGlobal")
}
