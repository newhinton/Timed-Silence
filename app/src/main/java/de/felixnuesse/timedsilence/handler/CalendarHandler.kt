package de.felixnuesse.timedsilence.handler

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

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

    private lateinit var cachedCalendars: ArrayList<CalendarObject>
    private var alreadyCached: Boolean=false

    fun getCalendarVolumeSetting(externalId: Long):Int{
        getCalendars(context)
        for (calObject in cachedCalendars){
            if(calObject.ext_id==externalId){
                val db = DatabaseHandler(context)

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
        if(!alreadyCached && hasCalendarReadPermission(context)){
            cachedCalendars = ArrayList()
        }else{
            return
        }

        getCalendarReadPermission(context)

        Log.e(Constants.APP_NAME, "test")
        val cursor: Cursor
        val contentResolver = context.contentResolver


        if (android.os.Build.VERSION.SDK_INT <= 7) {
            cursor = contentResolver!!.query(
                Uri.parse("content://calendar/calendars"),
                arrayOf("_id", "displayName",  "eventColor"),
                null,
                null,
                null
            )

        } else if (android.os.Build.VERSION.SDK_INT <= 14) {
            cursor = contentResolver!!.query(
                Uri.parse("content://com.android.calendar/calendars"),
                arrayOf("_id", "displayName",  CalendarContract.Calendars.CALENDAR_COLOR), null, null, null
            )

        } else {
            cursor = contentResolver!!.query(Uri.parse("content://com.android.calendar/calendars"), arrayOf("_id", "calendar_displayName",  CalendarContract.Calendars.CALENDAR_COLOR), null, null, null)

        }

        // Get calendars name
        Log.i(Constants.APP_NAME,"Cursor count " + cursor.count)
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
            alreadyCached=true
        } else {
            Log.e(APP_NAME,"No calendar found in the device")
        }
    }


    /*fun getNextOrCurrent(context: Context): ArrayList<String> {
        getCalendars(context)
        //readCalendarEvent()

    }*/

    fun readCalendarEvent(): ArrayList<Map<String, String>> {
        val cursor = context.getContentResolver()
            .query(
                Uri.parse("content://com.android.calendar/events"),
                arrayOf("calendar_id", CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND,CalendarContract.Events.ALL_DAY, CalendarContract.Events.DURATION ),
                null,
                null,
                null
            )
        cursor.moveToFirst()
        // fetching calendars name

        val retval: ArrayList<Map<String, String>> = ArrayList()
        val lengthDummyArray = arrayOfNulls<String>(cursor.count)

        // fetching calendars id
        CalendarEventFragment.nameOfEvent.clear()
        CalendarEventFragment.startDates.clear()
        CalendarEventFragment.endDates.clear()
        CalendarEventFragment.descriptions.clear()


        for (i in lengthDummyArray) {



            val map = HashMap<String, String>()
            map.put("calendar_id",cursor.getString(0))
            map.put("name_of_event",cursor.getString(1))
            map.put("start_date",cursor.getString(3))
            map.put("end_date",cursor.getString(4))
            map.put("description",cursor.getString(1))
            map.put("duration",cursor.getString(5))
            map.put("all_day",cursor.getString(6))
            retval.add(map)
            cursor.moveToNext()

        }

        Collections.sort(retval, this.MyMapComparator())
        return retval
    }

    fun getDate(milliSeconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        return formatter.format(calendar.getTime())
    }

    internal inner class MyMapComparator : Comparator<Map<String, String>> {
        override fun compare(o1: Map<String, String>, o2: Map<String, String>): Int {

            val s1 = o1["start_date"]!!.toLong()
            val s2 = o2["start_date"]!!.toLong()

            if(s1>s2){
                return -1
            }

            if(s1<s2){
                return 1
            }

            if(s1==s2){
                return 0
            }
            return 0

        }
    }
}