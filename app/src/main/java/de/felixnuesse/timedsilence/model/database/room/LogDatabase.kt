package de.felixnuesse.timedsilence.model.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.felixnuesse.timedsilence.handler.volume.VolumeState

@Database(entities = [VolumeState::class], version = 1)
abstract class LogDatabase : RoomDatabase() {

    abstract fun logEntryDao(): VolumeStateDao

    companion object {
        fun get(context: Context): LogDatabase {
            return Room.databaseBuilder(
                context,
                LogDatabase::class.java,
                "TimedSilence-Logentries.db"
            ).build()
        }
    }
}