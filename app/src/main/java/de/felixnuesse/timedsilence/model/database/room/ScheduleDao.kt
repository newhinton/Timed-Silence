package de.felixnuesse.timedsilence.model.database.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import de.felixnuesse.timedsilence.model.data.ScheduleObject

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM timetable")
    fun getAllSchedules(): List<ScheduleObject>


    @Query("SELECT * FROM timetable")
    fun getSchedulesForWeekday(): List<ScheduleObject>


    @Query("SELECT * FROM timetable WHERE id LIKE :id LIMIT 1")
    fun getScheduleByID(id: Long): ScheduleObject


    /**
     * Deletes ScheduleObject with the given id
     * @param id Switch to delete
     * @return amount of rows affected
     */
    @Query("DELETE FROM timetable WHERE id LIKE :id")
    fun deleteScheduleEntry(id: Long)

    @Delete
    fun deleteScheduleEntry(scheduleObject: ScheduleObject)

    /**
     * Creates a switch entry
     * @param so Switch to create
     * @return id
     */
    @Insert
    fun createScheduleEntry(so: ScheduleObject): Long

    @Update
    fun updateScheduleEntry(so: ScheduleObject)
}