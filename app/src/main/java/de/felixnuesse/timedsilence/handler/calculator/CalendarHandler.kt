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
import de.felixnuesse.timedsilence.Utils
import java.time.Duration
import java.time.format.DateTimeParseException
import java.util.regex.Pattern


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
            var permissions = true
            permissions = permissions && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED

            val permissionsList = Array(1) {Manifest.permission.ACCESS_FINE_LOCATION}

            if (!permissions)
                ActivityCompat.requestPermissions(context as Activity,permissionsList , Constants.CALENDAR_PERMISSION_REQUEST_ID)

        }

        fun hasCalendarReadPermission(context: Context):Boolean{
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
        }
    }


    var context: Context = context
    val db = DatabaseHandler(context)

    fun enableCaching(caching: Boolean){
        db.setCaching(caching)
    }

    private lateinit var cachedCalendars: ArrayList<CalendarObject>
    private lateinit var cachedCalendarEvents: ArrayList<Map<String, String>>
    private var alreadyCachedCalendars: Boolean=false
    private var alreadyCachedEvents: Boolean=false

    fun getCalendarVolumeSetting(externalId: Long):Int{
        getCalendars(context)
        for (calObject in cachedCalendars){
            //Log.d(APP_NAME, "CalendarHandler: getCalendarVolumeSetting:" + externalId + " | " + calObject.name+ " | " + calObject.id+ " | " + calObject.ext_id)
            if(calObject.ext_id==externalId){
                var calObject = db.getCalendarEntryByExtId(calObject.ext_id.toString())

                if(calObject==null){
                    return -1
                }
                return calObject.volume
            }
        }
        return -1
    }

    fun getCalendarColor(externalId: Long): Int{
        getCalendars(context)
        for (calObject in cachedCalendars){
            if(calObject.ext_id==externalId){
                return calObject.color
            }
        }
        return 0
    }

    fun getCalendarName(externalId: Long): String{
        getCalendars(context)
        for (calObject in cachedCalendars){
            if(calObject.ext_id==externalId){
                return calObject.name
            }
        }
        return "NOTSET"
    }

    fun getCalendars(): ArrayList<CalendarObject>{
        getCalendars(context)
        if (::cachedCalendars.isInitialized) { return cachedCalendars }
        return ArrayList<CalendarObject>()
    }

    private fun getCalendars(context: Context){
        if(!alreadyCachedCalendars && hasCalendarReadPermission(
                context
            )
        ){
            cachedCalendars = ArrayList()
        }else{
            return
        }

        getCalendarReadPermission(
            context
        )

        val contentResolver = context.contentResolver
        val cursor = contentResolver!!.query(
            Uri.parse("content://com.android.calendar/calendars"),
            arrayOf("_id", "calendar_displayName",
                CalendarContract.Calendars.CALENDAR_COLOR),
            null,
            null,
            null
        )

        // Get calendars name
        if (cursor.count > 0) {
            cursor.moveToFirst()

            for (i in 0 until cursor.count) {

                var calentry = CalendarObject(0, 0, Constants.TIME_SETTING_SILENT)

                calentry.ext_id=cursor.getInt(0).toLong()
                calentry.color=cursor.getInt(2)
                calentry.name=cursor.getString(1)

                cachedCalendars.add(calentry)
                cursor.moveToNext()
            }
            alreadyCachedCalendars=true
        } else {
            Log.e(APP_NAME,"CalendarHandler: No calendar found in the device")
        }

        cursor.close()
    }

    fun readCalendarEvent(timeInMilliseconds: Long): ArrayList<Map<String, String>> {
        return readCalendarEvent(timeInMilliseconds, true)
    }

    fun readCalendarEvent(timeInMilliseconds: Long, cached: Boolean): ArrayList<Map<String, String>> {



        /*if(!hasCalendarReadPermission(context)){
            getCalendarReadPermission(context)
            return ArrayList<Map<String, String>>()
        }*/

        if(cached && alreadyCachedEvents){
            return cachedCalendarEvents
        }

        Log.e(APP_NAME, "CalendarHandler: CurrentTime in MS:"+ Utils.getDate(timeInMilliseconds.toString()))
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
            CalendarContract.Events.EVENT_LOCATION
        )

        var cursor = context.contentResolver
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
        nameOfEvent.clear()
        startDates.clear()
        endDates.clear()
        descriptions.clear()


        for (i in lengthDummyArray) {
            val map = HashMap<String, String>()
            map.put("calendar_id",cursor.getString(0))
            map.put("name_of_event",cursor.getString(1))
            map.put("description",cursor.getString(2))
            map.put("start_date",cursor.getString(3))
            map.put("end_date",cursor.getString(4))
            map.put("all_day",cursor.getString(5))
            map.put("duration",cursor.getString(6))
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

        cursor = context.contentResolver.query(
            builder.build(),
            projection,
            null,
            null,
            CalendarContract.Events.DTEND + " ASC"
        )

        if(cursor==null){
            Log.e(APP_NAME, "CalendarHandler: readCalendarEvent: no results!")
            return retval;
        }
        cursor.moveToFirst()
        // fetching calendars name

        lengthDummyArray = arrayOfNulls<String>(cursor.count)

        // fetching calendars id
        nameOfEvent.clear()
        startDates.clear()
        endDates.clear()
        descriptions.clear()


        for (i in lengthDummyArray) {
            val map = HashMap<String, String>()
            map["calendar_id"] = cursor.getString(0)
            map["name_of_event"] = cursor.getString(1)
            map["description"] = cursor.getString(2)

            val start= cursor.getString(3).toLong()
            //the start time is from the FIRST time the event happens, so adjust it
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = start
            calendar.set(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH))
            val newStart = calendar.timeInMillis
            map["start_date"] = newStart.toString()



            map["end_date"] = cursor.getString(4)
            map["all_day"] = cursor.getString(5)

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

            retval.add(map)
            cursor.moveToNext()
        }

        cursor.close()

        Collections.sort(retval, this.MyMapComparator())

        cachedCalendarEvents=retval
        alreadyCachedEvents=true
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