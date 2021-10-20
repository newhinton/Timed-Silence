package de.felixnuesse.timedsilence.handler.calculator

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment.Companion.descriptions
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment.Companion.endDates
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment.Companion.nameOfEvent
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment.Companion.startDates
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import java.util.*
import kotlin.collections.ArrayList
import android.text.format.DateUtils
import android.content.ContentUris
import androidx.viewpager.widget.ViewPager
import de.felixnuesse.timedsilence.Utils
import de.felixnuesse.timedsilence.handler.NotificationHandler
import de.felixnuesse.timedsilence.model.calendar.DeviceCalendar
import de.felixnuesse.timedsilence.model.calendar.SettingsCalendar
import java.time.Duration
import java.time.format.DateTimeParseException
import java.util.regex.Pattern
import kotlin.collections.HashMap


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

    fun readCalendarEvent(timeInMilliseconds: Long): ArrayList<Map<String, String>>  {
        return deviceCalendar.readCalendarEvent(timeInMilliseconds)
    }
}