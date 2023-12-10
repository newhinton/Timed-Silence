package de.felixnuesse.timedsilence.volumestate.calendar

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import de.felixnuesse.timedsilence.util.DateUtil
import de.felixnuesse.timedsilence.handler.permissions.CalendarAccess
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.model.data.CalendarObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


import android.provider.CalendarContract.Events.CALENDAR_ID
import android.provider.CalendarContract.Events.TITLE
import android.provider.CalendarContract.Events.DESCRIPTION
import android.provider.CalendarContract.Events.DTSTART
import android.provider.CalendarContract.Events.DTEND
import android.provider.CalendarContract.Events.ALL_DAY
import android.provider.CalendarContract.Events.DURATION
import android.provider.CalendarContract.Events.EVENT_LOCATION
import android.provider.CalendarContract.Events.STATUS
import android.provider.CalendarContract.Events.AVAILABILITY
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.calendar.SettingsCalendar
import de.felixnuesse.timedsilence.model.data.CachedArrayList
import de.felixnuesse.timedsilence.volumestate.DeterministicCalculationInterface
import java.time.ZoneId


class DeviceCalendar(private var mContext: Context) {

    companion object {
        fun getCalendarReadPermission(context: Context) {
            CalendarAccess.hasCalendarReadPermission(context, true)
        }

        fun hasCalendarReadPermission(context: Context): Boolean {
            return CalendarAccess.hasCalendarReadPermission(context)
        }

        val DEFAULT_NAME = "NOTSET"
        val DEFAULT_COLOR = 0
        val DEFAULT_VOLUME = -1
    }

    private val TAG: String = "DeviceCalendar"
    private var calendarCache = HashMap<Long, CalendarObject>()
    private val settingsCalendar = SettingsCalendar(mContext)

    fun getCalendarVolumeSetting(name: String):Int{
        val calObject = settingsCalendar.getCalendars()[name]
        return calObject?.volume ?: DEFAULT_VOLUME
    }

    fun getCalendarColor(externalId: Long): Int{
        val calObject = getCalendars()[externalId]
        return calObject?.color ?: DEFAULT_COLOR
    }

    fun getCalendarName(externalId: Long): String{
        val calObject = getCalendars()[externalId]
        return calObject?.name ?: DEFAULT_NAME
    }
    fun getVolumeCalendars(): ArrayList<CalendarObject> {
        val calendars = ArrayList<CalendarObject>()
        settingsCalendar.getCalendars().forEach { (key, value) ->
            calendars.add(value)
        }
        return calendars
    }

    fun getDeviceCalendars(): ArrayList<CalendarObject>{
        val calendars = ArrayList<CalendarObject>()
        getCalendars().forEach { (key, value) ->
            calendars.add(value)
        }
        return calendars
    }

    fun getCalendars(): HashMap<Long, CalendarObject> {
        if (calendarCache.size > 0) {
            return calendarCache
        }

        getCalendarReadPermission(mContext)

        val contentResolver = mContext.contentResolver
        val cursor = contentResolver!!.query(
            Uri.parse("content://com.android.calendar/calendars"),
            arrayOf(
                "_id",
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                CalendarContract.Calendars.CALENDAR_COLOR
            ),
            null,
            null,
            null
        )

        // Get calendars name
        if (cursor != null) {
            if (cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    var calentry = CalendarObject(0, 0, TIME_SETTING_SILENT)

                    calentry.externalID = cursor.getInt(0).toLong()
                    calentry.color = cursor.getInt(2)
                    calentry.name = cursor.getString(1)

                    calendarCache[calentry.externalID] = calentry
                    cursor.moveToNext()
                }
            } else {
                Log.e(TAG, "CalendarHandler: No calendar found in the device")
            }
        }
        cursor?.close()
        return calendarCache
    }

    private fun getProjection(): Array<String> {
        return arrayOf(
            CALENDAR_ID,
            TITLE,
            DESCRIPTION,
            DTSTART,
            DTEND,
            ALL_DAY,
            DURATION,
            EVENT_LOCATION,
            STATUS,
            AVAILABILITY
        )
    }

}