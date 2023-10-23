package com.example.phase2

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {

    companion object {
        const val DATABASE_NAME = "Users.db"
        const val TABLE_NAME = "user_table"
        const val COL_1 = "ID"
        const val COL_2 = "EMAIL"
        const val COL_3 = "PASSWORD"

        private const val TABLE_DRAWINGS = "drawings"
        private const val COLUMN_ID = "id"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_DRAWING_NAME = "drawing_name"
        private const val COLUMN_DRAWING_DATA = "drawing_data"
        private const val COLUMN_DRAWING_IMAGES = "drawing_images"
        private const val COLUMN_DRAWING_TEXTS = "drawing_texts"
    }



    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE $TABLE_NAME (ID INTEGER PRIMARY KEY AUTOINCREMENT, EMAIL TEXT, PASSWORD TEXT)")

        val createDrawingsTableQuery = """
            CREATE TABLE IF NOT EXISTS $TABLE_DRAWINGS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USER_ID INTEGER,
                $COLUMN_DRAWING_NAME TEXT,
                $COLUMN_DRAWING_DATA TEXT,
                $COLUMN_DRAWING_IMAGES TEXT,
                 $COLUMN_DRAWING_TEXTS TEXT
            )
        """.trimIndent()

        db?.execSQL(createDrawingsTableQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun insertData(email: String, password: String): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_2, email)
        contentValues.put(COL_3, password)
        val result = db.insert(TABLE_NAME, null, contentValues)
        return !result.equals(-1)
    }

    fun checkUser(email: String, password: String): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE EMAIL = ? AND PASSWORD = ?", arrayOf(email, password))
        val exists = cursor.count > 0
        cursor.close()  // Close the cursor
        db.close()  // Close the database connection
        return exists
    }


    fun getUser(email: String, password: String): Long {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT ID FROM $TABLE_NAME WHERE EMAIL = ? AND PASSWORD = ? LIMIT  1", arrayOf(email, password))
        var exists = -1L  // Initialize with a default value

        if (cursor.moveToFirst()) {
            // Move to the first row (if available)
            exists = cursor.getLong(0)  // Get the value at the first column (zero-based index)
        }

        cursor.close()  // Close the cursor
        db.close()  // Close the database connection
        return exists
    }

//    fun insertDrawing(userId: Int,drawingName: String, drawingData: Any): Long {
//        Log.e("data", drawingData.toString())
//        val db = this.writableDatabase
//        val contentValues = ContentValues()
//        contentValues.put(COLUMN_USER_ID, userId)
//        contentValues.put(COLUMN_DRAWING_NAME, drawingName)
//        contentValues.put(COLUMN_DRAWING_DATA, drawingData.toString())
//        return db.insert(TABLE_DRAWINGS, null, contentValues)
////        return true
//    }
//
//    fun getDrawingByDrawingName(userID: Long, drawingName: String): Any? {
//        val db = this.readableDatabase
//        val query = "SELECT * FROM $TABLE_DRAWINGS WHERE $COLUMN_USER_ID = ? AND $COLUMN_DRAWING_NAME = ?"
//        val cursor = db.rawQuery(query, arrayOf(userID.toString(), drawingName))
//
//        var drawing: Drawing? = null
//
//        if (cursor.moveToFirst()) {
//            val idIndex = cursor.getColumnIndex(COLUMN_ID)
//            val drawingDataIndex = cursor.getColumnIndex(COLUMN_DRAWING_DATA)
//
//            if (idIndex != -1 && drawingDataIndex != -1) {
//                val drawingId = cursor.getLong(idIndex)
//                val drawingData = cursor.getString(drawingDataIndex)
//                drawing = Drawing(drawingId, userID, drawingName, drawingData)
//            }
//        }
//
//        cursor.close()
//        return drawing
//    }
}
