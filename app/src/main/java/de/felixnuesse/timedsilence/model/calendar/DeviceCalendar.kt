package de.felixnuesse.timedsilence.model.calendar

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.text.format.DateUtils
import android.util.Log
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.util.DateUtil
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
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
import de.felixnuesse.timedsilence.model.data.CachedArrayList


class DeviceCalendar(private var mContext: Context) {

    companion object {
        fun getCalendarReadPermission(context: Context) {
            CalendarAccess.hasCalendarReadPermission(context, true)
        }

        fun hasCalendarReadPermission(context: Context): Boolean {
            return CalendarAccess.hasCalendarReadPermission(context)
        }
    }

    private val TAG: String = "DeviceCalendar"
    private var calendarCache = HashMap<String, CalendarObject>()
    private var mEventList = CachedArrayList<DeviceCalendarEventModel>()
    private var mCachingEnabled = false

    private var mIgnoreAllDayEvents = SharedPreferencesHandler.getPref(
        mContext,
        PrefConstants.PREF_IGNORE_ALL_DAY_EVENTS,
        PrefConstants.PREF_IGNORE_ALL_DAY_EVENTS_DEFAULT
    )
    private var mIgnoreTentativeEvents = SharedPreferencesHandler.getPref(
        mContext,
        PrefConstants.PREF_IGNORE_TENTATIVE_EVENTS,
        PrefConstants.PREF_IGNORE_TENTATIVE_EVENTS_DEFAULT
    )
    private var mIgnoreCancelledEvents = SharedPreferencesHandler.getPref(
        mContext,
        PrefConstants.PREF_IGNORE_CANCELLED_EVENTS,
        PrefConstants.PREF_IGNORE_CANCELLED_EVENTS_DEFAULT
    )
    private var mIgnoreFreeEvents = SharedPreferencesHandler.getPref(
        mContext,
        PrefConstants.PREF_IGNORE_FREE_EVENTS,
        PrefConstants.PREF_IGNORE_FREE_EVENTS_DEFAULT
    )


    fun getCalendars(): HashMap<String, CalendarObject> {
        if (calendarCache.size > 0) {
            return calendarCache
        }

        getCalendarReadPermission(mContext)

        val contentResolver = mContext.contentResolver
        val cursor = contentResolver!!.query(
            Uri.parse("content://com.android.calendar/calendars"),
            arrayOf(
                "_id", "calendar_displayName",
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

                    calentry.ext_id = cursor.getInt(0).toLong()
                    calentry.color = cursor.getInt(2)
                    calentry.name = cursor.getString(1)

                    calendarCache[calentry.name] = calentry
                    cursor.moveToNext()
                }
            } else {
                Log.e(TAG, "CalendarHandler: No calendar found in the device")
            }
        }
        cursor?.close()
        return calendarCache
    }


    fun setCaching(caching: Boolean){
        mCachingEnabled=caching
    }

    fun readCalendarEvent(
        timeInMilliseconds: Long
    ): ArrayList<DeviceCalendarEventModel> {
        if (mEventList.cacheInitialized && mCachingEnabled) {
            return mEventList
        }

        if (!hasCalendarReadPermission(mContext)) {
            return ArrayList()
        }

        Log.e(
            TAG,
            "CalendarHandler: CurrentTime in MS: " + DateUtil.getDate(timeInMilliseconds.toString())
        )
        val startTime = Calendar.getInstance()

        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.SECOND, 0)

        val endTime = Calendar.getInstance()
        endTime.add(Calendar.DATE, 1)


        // Construct the query with the desired date range.
        val builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon()
        val now = Date().time // - (DateUtils.HOUR_IN_MILLIS + DateUtils.MINUTE_IN_MILLIS*30)
        val range = DateUtils.HOUR_IN_MILLIS * 12
        ContentUris.appendId(builder, now - range)
        ContentUris.appendId(builder, now + range)

        var cursor = mContext.contentResolver.query(
            builder.build(),
            getProjection(),
            null,
            null,
            "$DTEND ASC"
        )

        if (cursor == null) {
            Log.e(TAG, "readCalendarEvent: no results!")
            return ArrayList()
        }

        cursor.moveToFirst()

        var eventList: ArrayList<DeviceCalendarEventModel> = ArrayList()

        for (i in 0..<cursor.count) {

            var event = DeviceCalendarEventModel(mContext)

            event.mCalendarID = cursor.getInt(0)
            event.setTitle(cursor.getString(1))
            event.setDescription(cursor.getString(2))
            event.setDtstart(cursor.getLong(3))
            event.setOrCalculateDtend(cursor.getLong(4), cursor.getString(6)?: "")
            event.mAllDay = cursor.getInt(5) == 1
            event.mStatus = cursor.getInt(8)
            event.mAvailability = cursor.getInt(9)

            if (!event.shouldEventBeExcluded(mIgnoreAllDayEvents, mIgnoreTentativeEvents, mIgnoreCancelledEvents, mIgnoreFreeEvents)) {
                eventList.add(event)
            }

            cursor.moveToNext()
        }

        cursor.close()

        Collections.sort(eventList, this.DeviceCalendarEventModelComparator())
        mEventList.set(eventList)
        return mEventList
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

    internal inner class DeviceCalendarEventModelComparator : Comparator<DeviceCalendarEventModel> {
        override fun compare(o1: DeviceCalendarEventModel, o2: DeviceCalendarEventModel): Int {

            val s1 = o1.mStart
            val s2 = o2.mStart

            if (s1 < s2) {
                return -1
            }

            if (s1 > s2) {
                return 1
            }

            if (s1 == s2) {
                return 0
            }
            return 0

        }
    }
}