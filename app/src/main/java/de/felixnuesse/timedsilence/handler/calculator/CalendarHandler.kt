package de.felixnuesse.timedsilence.handler.calculator

import android.content.Context
import de.felixnuesse.timedsilence.model.data.CalendarObject
import kotlin.collections.ArrayList
import de.felixnuesse.timedsilence.model.calendar.DeviceCalendar
import de.felixnuesse.timedsilence.model.calendar.DeviceCalendarEventModel
import de.felixnuesse.timedsilence.model.calendar.SettingsCalendar


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 07.11.19 - 12:13
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
class CalendarHandler(context: Context) {

    companion object {
        fun getCalendarReadPermission(context: Context) {
            DeviceCalendar.getCalendarReadPermission(context)
        }

        fun hasCalendarReadPermission(context: Context):Boolean{
            return DeviceCalendar.hasCalendarReadPermission(context)
        }

        val DEFAULT_NAME = "NOTSET"
        val DEFAULT_COLOR = 0
        val DEFAULT_VOLUME = -1
    }

    private val deviceCalendar = DeviceCalendar(context)
    private val settingsCalendar = SettingsCalendar(context)

    init {
        deviceCalendar.setCaching(true)
    }


    fun getCalendarVolumeSetting(name: String):Int{
        val calObject = settingsCalendar.getCalendars()[name]
        return calObject?.volume ?: DEFAULT_VOLUME
    }

    fun getCalendarColor(name: String): Int{
        val calObject = deviceCalendar.getCalendars()[name]
        return calObject?.color ?: DEFAULT_COLOR
    }

    fun getCalendarName(externalId: Long): String{
        var name = DEFAULT_NAME
        deviceCalendar.getCalendars().forEach { (_, value) ->
            if(value.ext_id==externalId){
                name = value.name
            }
        }
        return name
    }

    fun getDeviceCalendars(): ArrayList<CalendarObject>{
        val calendars = ArrayList<CalendarObject>()
        deviceCalendar.getCalendars().forEach { (key, value) ->
            calendars.add(value)
        }
        return calendars
    }
    fun getVolumeCalendars(): ArrayList<CalendarObject> {
        val calendars = ArrayList<CalendarObject>()
        settingsCalendar.getCalendars().forEach { (key, value) ->
            calendars.add(value)
        }
        return calendars
    }

    fun getFilteredEventsForDay(timeInMilliseconds: Long): ArrayList<DeviceCalendarEventModel>  {
        return deviceCalendar.getEventsForDay(timeInMilliseconds)
    }
}