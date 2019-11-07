package de.felixnuesse.timedsilence.handler

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.fragments.CalendarEventFragment
import de.felixnuesse.timedsilence.model.data.CalendarObject
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

    var context: Context = context

    private lateinit var cachedCalendars: ArrayList<CalendarObject>
    private var alreadyCached: Boolean=false

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

    private fun getCalendars(context: Context){
        if(!alreadyCached){
            cachedCalendars = ArrayList()
        }else{
            return
        }

        this.getCalendarPermission(42, context, Manifest.permission.READ_CALENDAR)

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

                var calentry = CalendarObject(0, 0, 0)

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

    private fun getCalendarPermission(callbackId: Int, context: Context, vararg permissionsId: String) {
        var permissions = true
        for (p in permissionsId) {
            permissions =
                permissions && ContextCompat.checkSelfPermission(context, p) == PackageManager.PERMISSION_GRANTED
        }

        if (!permissions)
            ActivityCompat.requestPermissions(context as Activity, permissionsId, callbackId)
    }


    private fun readCalendarEvent(context: Context): ArrayList<String> {
        val cursor = context.getContentResolver()
            .query(
                Uri.parse("content://com.android.calendar/events"),
                arrayOf("calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"),
                null,
                null,
                null
            )
        cursor.moveToFirst()
        // fetching calendars name
        val CNames = arrayOfNulls<String>(cursor.getCount())

        // fetching calendars id
        CalendarEventFragment.nameOfEvent.clear()
        CalendarEventFragment.startDates.clear()
        CalendarEventFragment.endDates.clear()
        CalendarEventFragment.descriptions.clear()
        for (i in CNames.indices) {

            CalendarEventFragment.nameOfEvent.add(cursor.getString(1))
            CalendarEventFragment.startDates.add(getDate(java.lang.Long.parseLong(cursor.getString(3))))
            //endDates.add(getDate(java.lang.Long.parseLong(cursor.getString(4))))
            CalendarEventFragment.descriptions.add(cursor.getString(2))
            CNames[i] = cursor.getString(1)
            cursor.moveToNext()

        }
        return CalendarEventFragment.nameOfEvent
    }

    private fun getDate(milliSeconds: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm:ss a")
        val calendar = Calendar.getInstance()
        calendar.setTimeInMillis(milliSeconds)
        return formatter.format(calendar.getTime())
    }
}