package de.felixnuesse.timedsilence.volumestate.calendar

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.util.Log
import de.felixnuesse.timedsilence.util.DateUtil
import java.util.*
import kotlin.collections.ArrayList


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
import de.felixnuesse.timedsilence.Constants.Companion.REASON_CALENDAR
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.data.CachedArrayList
import de.felixnuesse.timedsilence.volumestate.DeterministicCalculationInterface
import java.time.ZoneId


open class Events(private var mContext: Context): DeterministicCalculationInterface() {

    private var mEventList = CachedArrayList<DeviceCalendarEventModel>()

    private var mPreferencesManager = PreferencesManager(mContext)
    private var mIgnoreAllDayEvents = mPreferencesManager.ignoreAllday()
    private var mIgnoreTentativeEvents = mPreferencesManager.ignoreTentative()
    private var mIgnoreCancelledEvents = mPreferencesManager.ignoreCancelled()
    private var mIgnoreFreeEvents = mPreferencesManager.ignoreFree()

    private val mDeviceCalendar = DeviceCalendar(mContext)


    //Todo: Move that out and chache it for events too. This takes a while.
    fun getEventsForDay(): ArrayList<DeviceCalendarEventModel> {
        if (mEventList.cacheInitialized) {
            return mEventList
        }

        if (!DeviceCalendar.hasCalendarReadPermission(mContext)) {
            return ArrayList()
        }

        // Construct the query with the desired date range.
        val builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon()

        ContentUris.appendId(builder, DateUtil.getMidnight(date).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
        ContentUris.appendId(builder, DateUtil.getMidnight(date).plusHours(24).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())

        var cursor = mContext.contentResolver.query(
            builder.build(),
            getProjection(),
            null,
            null,
            "$DTSTART ASC"
        )

        if (cursor == null) {
            Log.e(TAG(),"readCalendarEvent: no results!")
            return ArrayList()
        }

        cursor.moveToFirst()

        var eventList: ArrayList<DeviceCalendarEventModel> = ArrayList()
        for (i in 0..<cursor.count) {

            try {
                var event = DeviceCalendarEventModel(mContext, date)

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

            } catch (e: Exception) {
                LogHandler.writeLog(mContext, "DeviceCalendar", "Exception!", "${e.toString()}");
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

            return 0

        }
    }

    override fun stateAt(timeInMs: Long): ArrayList<VolumeState> {
        return ArrayList()
    }

    override fun states(): ArrayList<VolumeState> {

        val list = ArrayList<VolumeState>()

        getEventsForDay().forEach {

            val calendarName = mDeviceCalendar.getCalendarName(it.mCalendarID.toLong())
            val volume = mDeviceCalendar.getCalendarVolumeSetting(calendarName)
            if(volume != DeviceCalendar.DEFAULT_VOLUME){
                val vs = VolumeState(volume)
                vs.startTime = it.mStart
                vs.endTime = it.mEnd

                vs.setReason(REASON_CALENDAR, "$calendarName (${it.mTitle})")

                list.add(vs)
            }
        }

        return list
    }

    override fun isEnabled(): Boolean {
        return DeviceCalendar.hasCalendarReadPermission(mContext)
    }
}