package de.felixnuesse.timedsilence.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.DayOfWeek

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 13.04.19 - 19:41
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 *
 * This program is released under the GPLv3 license
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 *
 *
 *
 */
@Serializable
@Entity(tableName="timetable")
data class ScheduleObject(

    @ColumnInfo(name = "SCHEDULE_NAME")
    var name: String?,

    @ColumnInfo(name = "time_start")
    var timeStart: Long?,

    @ColumnInfo(name = "time_end")
    var timeEnd: Long?,

    @ColumnInfo(name = "schedule_volume")
    var timeSetting: Int?,

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Long?
) {


    constructor(name: String, timeStart: Long, timeEnd: Long, timeSetting: Int, id: Long,
                pmon: Boolean, ptue: Boolean, pwed: Boolean, pthu: Boolean, pfri: Boolean, psat: Boolean, psun: Boolean
    ) : this(name, timeStart, timeEnd, timeSetting, id){

        mon=pmon
        tue=ptue
        wed=pwed
        thu=pthu
        fri=pfri
        sat=psat
        sun=psun

    }

    @ColumnInfo(name = "schedule_active_mon")
    var mon: Boolean = false
    @ColumnInfo(name = "schedule_active_tue")
    var tue: Boolean = false
    @ColumnInfo(name = "schedule_active_wed")
    var wed: Boolean = false
    @ColumnInfo(name = "schedule_active_thu")
    var thu: Boolean = false
    @ColumnInfo(name = "schedule_active_fri")
    var fri: Boolean = false
    @ColumnInfo(name = "schedule_active_sat")
    var sat: Boolean = false
    @ColumnInfo(name = "schedule_active_sun")
    var sun: Boolean = false

    fun isValidOnWeekday(weekday: DayOfWeek): Boolean {
        if(weekday == DayOfWeek.MONDAY && mon) return true
        if(weekday == DayOfWeek.TUESDAY && tue) return true
        if(weekday == DayOfWeek.WEDNESDAY && wed) return true
        if(weekday == DayOfWeek.THURSDAY && thu) return true
        if(weekday == DayOfWeek.FRIDAY && fri) return true
        if(weekday == DayOfWeek.SATURDAY && sat) return true
        if(weekday == DayOfWeek.SUNDAY && sun) return true

        return false
    }

}