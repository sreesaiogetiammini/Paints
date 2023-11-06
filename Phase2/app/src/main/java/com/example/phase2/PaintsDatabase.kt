package com.example.phase2

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(
    entities = [PaintsData :: class],
    version = 6,
    exportSchema = false
)
abstract class PaintsDatabase : RoomDatabase() {
    abstract fun PaintsDao() : PaintsDao

    companion object {
        @Volatile
        private var INSTANCE: PaintsDatabase? = null

        fun getDatabase(context: Context): PaintsDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PaintsDatabase::class.java,
                    "paints_databases"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}

@Dao
interface PaintsDao {
    @Insert
    suspend fun insertPaintsData(data: PaintsData)

//    @Query("SELECT * FROM paints WHERE drawingName = :drawingName AND userId = :userId LIMIT 1")
//    suspend fun getDrawingByDrawingName(drawingName: String, userId: String): PaintsData?

    @Query("SELECT * FROM paints WHERE (drawingName = :drawingName AND userId = :userId) OR (drawingName = :drawingName AND isGlobal = 1) LIMIT 1")
    suspend fun getDrawingByDrawingName(drawingName: String, userId: String): PaintsData?


    @Query("DELETE FROM paints WHERE drawingName = :drawingName AND userId = :userId")
    suspend fun deletePaintingByDrawingName(drawingName: String, userId: String): Int

    @Update
    suspend fun updatePaintsData(paintsData: PaintsData)

    @Query("SELECT * FROM paints WHERE userId = :userId")
    fun getPaintingsByUserId(userId: String): List<PaintsData>

    @Query("SELECT drawingName FROM paints WHERE userId = :userId")
    fun getDrawingNamesByUserId(userId: String): List<String>

    @Query("UPDATE paints SET isGlobal = 1 WHERE drawingName = :drawingName AND userId = :userId")
    suspend fun setDrawingAsGlobal(drawingName: String, userId: String): Int




//    @Query("SELECT * FROM paints")
//    suspend fun getAllPaintsData(): LiveData<List<PaintsData>>
}