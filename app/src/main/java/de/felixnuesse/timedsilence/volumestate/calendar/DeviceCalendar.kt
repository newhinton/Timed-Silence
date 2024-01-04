package de.felixnuesse.timedsilence.volumestate.calendar

import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.util.Log
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.model.data.CalendarObject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import de.felixnuesse.timedsilence.model.calendar.SettingsCalendar
import de.felixnuesse.timedsilence.util.PermissionManager


class DeviceCalendar(private var mContext: Context) {

    companion object {
        fun getCalendarReadPermission(context: Context) {
            PermissionManager(context).requestCalendarAccess()
        }

        fun hasCalendarReadPermission(context: Context): Boolean {
            return PermissionManager(context).grantedCalendar()
        }

        val DEFAULT_NAME = "NOTSET"
        val DEFAULT_COLOR = 0
        val DEFAULT_VOLUME = -1
    }

    private var calendarCache = HashMap<String, CalendarObject>()
    private val settingsCalendar = SettingsCalendar(mContext)

    fun getCalendarVolumeSetting(name: String):Int {
        val calObject = settingsCalendar.getCalendars()[name]
        return calObject?.volume ?: DEFAULT_VOLUME
    }

    //Todo: figure out why this is needed, and the color attribute is not used in eg. CalendarListAdapter
    fun getCalendarColor(name: String): Int {
        val calObject = getCalendars()[name]
        return calObject?.color ?: DEFAULT_COLOR
    }

    /**
     * This function gets the local, device-specific id for a given calendar name.
     * You can use this id to get the other information.
     */
    fun getCalendarName(externalId: Long): String {
        getCalendars().forEach {(_, value) ->
            if(value.externalID == externalId) {
                return value.name
            }
        }
        return DEFAULT_NAME
    }
    fun getVolumeCalendars(): ArrayList<CalendarObject> {
        val calendars = ArrayList<CalendarObject>()
        settingsCalendar.getCalendars().forEach { (_, value) ->
            calendars.add(value)
        }
        return calendars
    }

    fun getDeviceCalendars(): ArrayList<CalendarObject>{
        val calendars = ArrayList<CalendarObject>()
        getCalendars().forEach { (_, value) ->
            calendars.add(value)
        }
        return calendars
    }

    fun getCalendars(): HashMap<String, CalendarObject> {
        if (calendarCache.size > 0) {
            return calendarCache
        }

        if(!hasCalendarReadPermission(mContext)) {
            getCalendarReadPermission(mContext)
        }

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

                    calendarCache[calentry.name] = calentry
                    cursor.moveToNext()
                }
            } else {
                Log.e(TAG(), "CalendarHandler: No calendar found in the device")
            }
        }
        cursor?.close()
        return calendarCache
    }
}