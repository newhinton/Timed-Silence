package de.felixnuesse.timedsilence.model.calendar

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import android.text.format.DateUtils
import android.util.Log
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.util.DateUtil
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.permissions.CalendarAccess
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.model.data.CalendarObject
import java.time.Duration
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

import android.provider.CalendarContract.Events.STATUS
import android.provider.CalendarContract.Events.AVAILABILITY


import android.provider.CalendarContract.Events.STATUS_TENTATIVE
import android.provider.CalendarContract.Events.STATUS_CANCELED
import android.provider.CalendarContract.Events.AVAILABILITY_FREE

class DeviceCalendar(private var mContext: Context) {

    companion object {
        fun getCalendarReadPermission(context: Context) {
            CalendarAccess.hasCalendarReadPermission(context, true)
        }

        fun hasCalendarReadPermission(context: Context):Boolean{
            return CalendarAccess.hasCalendarReadPermission(context)
        }
    }

    private val TAG: String = "DeviceCalendar"
    private var calendarCache = HashMap<String, CalendarObject>()
    private var eventCache = ArrayList<Map<String, String>>()

    private var validEventCacheButEmptyCache = false

    private var mIgnoreAllDayEvents = SharedPreferencesHandler.getPref(mContext, PrefConstants.PREF_IGNORE_ALL_DAY_EVENTS, PrefConstants.PREF_IGNORE_ALL_DAY_EVENTS_DEFAULT)
    private var mIgnoreTentativeEvents = SharedPreferencesHandler.getPref(mContext, PrefConstants.PREF_IGNORE_TENTATIVE_EVENTS, PrefConstants.PREF_IGNORE_TENTATIVE_EVENTS_DEFAULT)
    private var mIgnoreCancelledEvents = SharedPreferencesHandler.getPref(mContext, PrefConstants.PREF_IGNORE_CANCELLED_EVENTS, PrefConstants.PREF_IGNORE_CANCELLED_EVENTS_DEFAULT)
    private var mIgnoreFreeEvents = SharedPreferencesHandler.getPref(mContext, PrefConstants.PREF_IGNORE_FREE_EVENTS, PrefConstants.PREF_IGNORE_FREE_EVENTS_DEFAULT)


    fun getCalendars(): HashMap<String, CalendarObject> {
        if(calendarCache.size>0){
            return calendarCache
        }

        getCalendarReadPermission(mContext)

        val contentResolver = mContext.contentResolver
        val cursor = contentResolver!!.query(
            Uri.parse("content://com.android.calendar/calendars"),
            arrayOf("_id", "calendar_displayName",
                    CalendarContract.Calendars.CALENDAR_COLOR),
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

                    calentry.ext_id=cursor.getInt(0).toLong()
                    calentry.color=cursor.getInt(2)
                    calentry.name=cursor.getString(1)

                    calendarCache[calentry.name] = calentry
                    cursor.moveToNext()
                }
            } else {
                Log.e(TAG,"CalendarHandler: No calendar found in the device")
            }
        }
        cursor?.close()
        return calendarCache
    }


    fun readCalendarEvent(timeInMilliseconds: Long): ArrayList<Map<String, String>> {
        return readCalendarEvent(timeInMilliseconds, true)
    }

    //currently no cache
    //todo: implement caching
    fun readCalendarEvent(timeInMilliseconds: Long, cached: Boolean): ArrayList<Map<String, String>> {

        if(eventCache.size>0 || validEventCacheButEmptyCache){
            return eventCache
        }

        if(!hasCalendarReadPermission(mContext)){
            return eventCache
        }

        validEventCacheButEmptyCache = false
        Log.e(TAG, "CalendarHandler: CurrentTime in MS: "+ DateUtil.getDate(timeInMilliseconds.toString()))
        val startTime = Calendar.getInstance()

        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.SECOND, 0)

        val endTime = Calendar.getInstance()
        endTime.add(Calendar.DATE, 1)

        val projection = arrayOf(
            CalendarContract.Events.CALENDAR_ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DESCRIPTION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY,
            CalendarContract.Events.DURATION,
            CalendarContract.Events.EVENT_LOCATION,
            STATUS,
            AVAILABILITY
        )

        var cursor = mContext.contentResolver
            .query(
                Uri.parse("content://com.android.calendar/events"),
                projection, // arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                null,
                null,
                null
            )
        cursor!!.moveToFirst()


        var retval: ArrayList<Map<String, String>> = ArrayList()
        var lengthDummyArray = arrayOfNulls<String>(cursor.count)

        // fetching calendars id
        CalendarEventFragment.nameOfEvent.clear()
        CalendarEventFragment.startDates.clear()
        CalendarEventFragment.endDates.clear()
        CalendarEventFragment.descriptions.clear()


        for (i in lengthDummyArray) {
            val map = HashMap<String, String>()
            map.put("calendar_id",cursor.getString(0))
            map.put("name_of_event",cursor.getString(1))
            map.put("description",cursor.getString(2))
            map.put("start_date",cursor.getString(3))
            map.put("end_date",cursor.getString(4))
            map.put("all_day",cursor.getString(5))
            map.put("duration",cursor.getString(6))
            map.put(STATUS, cursor.getString(8))
            map.put(AVAILABILITY, cursor.getString(9))
            map.put("recurring","false")
            //retval.add(map)
            cursor.moveToNext()

        }
        cursor.close()


        // Construct the query with the desired date range.
        val builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon()
        val now = Date().time // - (DateUtils.HOUR_IN_MILLIS + DateUtils.MINUTE_IN_MILLIS*30)
        val range =  DateUtils.HOUR_IN_MILLIS*12
        ContentUris.appendId(builder, now - range)
        ContentUris.appendId(builder, now + range)

        cursor = mContext.contentResolver.query(
            builder.build(),
            projection,
            null,
            null,
            CalendarContract.Events.DTEND + " ASC"
        )

        if(cursor==null){
            Log.e(TAG, "CalendarHandler: readCalendarEvent: no results!")
            return retval;
        }
        cursor.moveToFirst()
        // fetching calendars name

        lengthDummyArray = arrayOfNulls<String>(cursor.count)

        // fetching calendars id
        CalendarEventFragment.nameOfEvent.clear()
        CalendarEventFragment.startDates.clear()
        CalendarEventFragment.endDates.clear()
        CalendarEventFragment.descriptions.clear()


        for (i in lengthDummyArray) {
            val map = HashMap<String, String>()
            map["calendar_id"] = cursor.getString(0)
            map["name_of_event"] = cursor?.getString(1) ?: mContext.getString(R.string.event_no_title)
            map["description"] = cursor?.getString(2) ?: mContext.getString(R.string.event_no_description)

            val start= cursor.getString(3).toLong()
            //the start time is from the FIRST time the event happens, so adjust it
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = start
            calendar.set(
                Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(
                    Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            val newStart = calendar.timeInMillis
            map["start_date"] = newStart.toString()



            map["end_date"] = cursor?.getString(4) ?: map["start_date"] as String
            map["all_day"] = cursor.getString(5)

            map[STATUS] = cursor.getInt(8).toString()
            map[AVAILABILITY] = cursor.getInt(9).toString()

            var recurring = cursor.getString(6) ?: ""

            //Log.d(APP_NAME, "CalendarHandler: RecurringPattern: "+recurring)
            if(recurring.equals("")){
                map["duration"] = cursor?.getString(6) ?: "0"
                map["recurring"] = "false"
            }else{

                //First, fix the damn issue with the missing T for seconds
                val sPattern = Pattern.compile("P\\d+S")
                if(sPattern.matcher(recurring).matches()){
                    val sb = StringBuffer(recurring)
                    sb.insert(recurring.indexOf("P")+1, "T")
                    recurring = sb.toString()
                    //Log.d(APP_NAME, "CalendarHandler: Fixed RecurringPattern: "+recurring)
                }

                val msEnd= Duration.parse(recurring).toMillis()
                val t = newStart+msEnd
                map["duration"] = msEnd.toString()
                map["end_date"] = t.toString()

                map["recurring"] = "true"
            }


            var isIgnoredEventType = false

            if(map["all_day"]?.toInt() == 1 && mIgnoreAllDayEvents){
                Log.d(TAG, "Event ${map["name_of_event"]} is all Day.")
                isIgnoredEventType = true
            }

            if(map[STATUS]?.toInt() == STATUS_TENTATIVE && mIgnoreTentativeEvents){
                Log.d(TAG, "Event ${map["name_of_event"]} is Tentative.")
                isIgnoredEventType = true
            }

            val bc2workaround = map["description"]?.contains("BC2-Status: Cancelled") ?: false
            if((map[STATUS]?.toInt() == STATUS_CANCELED || bc2workaround) && mIgnoreCancelledEvents){
                Log.d(TAG, "Event ${map["name_of_event"]} is Cancelled.")
                isIgnoredEventType = true
            }

            if(map[AVAILABILITY]?.toInt() == AVAILABILITY_FREE && mIgnoreFreeEvents){
                Log.d(TAG, "Event ${map["name_of_event"]} is Free.")
                isIgnoredEventType = true
            }

            if(!isIgnoredEventType){
                retval.add(map)
            }

            cursor.moveToNext()
        }

        cursor.close()

        Collections.sort(retval, this.MyMapComparator())
        eventCache.addAll(retval)
        validEventCacheButEmptyCache = true
        return retval
    }

    internal inner class MyMapComparator : Comparator<Map<String, String>> {
        override fun compare(o1: Map<String, String>, o2: Map<String, String>): Int {

            val s1 = o1["start_date"]?.toLong() ?: 0
            val s2 = o2["start_date"]?.toLong() ?: 0

            if(s1<s2){
                return -1
            }

            if(s1>s2){
                return 1
            }

            if(s1==s2){
                return 0
            }
            return 0

        }
    }
}