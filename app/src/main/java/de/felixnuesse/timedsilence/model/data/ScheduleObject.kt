package de.felixnuesse.timedsilence.model.data

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
data class ScheduleObject(var name: String, var timeStart: Long, var timeEnd: Long, var timeSetting: Int, var id: Long) {


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

    var mon: Boolean = false
    var tue: Boolean = false
    var wed: Boolean = false
    var thu: Boolean = false
    var fri: Boolean = false
    var sat: Boolean = false
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