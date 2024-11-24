package de.felixnuesse.timedsilence.model.database.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.felixnuesse.timedsilence.handler.volume.VolumeState

@Dao
interface VolumeStateDao {

    @Query("SELECT * FROM VolumeState ORDER BY id DESC LIMIT 50")
    fun getAllLoggedStates(): List<VolumeState>

    @Insert
    fun createLogEntry(entry: VolumeState): Long

}