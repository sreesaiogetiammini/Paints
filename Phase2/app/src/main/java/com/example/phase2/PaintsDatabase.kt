package com.example.phase2

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [PaintsData :: class],
    version = 1,
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
                ).build()
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

    @Query("SELECT * FROM paints WHERE drawingName = :drawingName AND userId = :userId LIMIT 1")
    suspend fun getDrawingByDrawingName(drawingName: String, userId: Long): PaintsData?

//    @Query("SELECT * FROM paints")
//    suspend fun getAllPaintsData(): LiveData<List<PaintsData>>
}