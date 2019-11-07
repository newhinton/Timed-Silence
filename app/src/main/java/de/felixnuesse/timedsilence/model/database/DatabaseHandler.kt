package de.felixnuesse.timedsilence.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.DATABASE_NAME
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.DATABASE_VERSION
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_END
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_NAME
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_SETTING
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_START
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_TABLE
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import android.content.ContentValues
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.model.data.CalendarObject
import de.felixnuesse.timedsilence.model.data.WifiObject
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_ANDROID_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_VOL_MODE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_MON
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_TUE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_WED
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_THU
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_FRI
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_SAT
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_SUN
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES_CALENDAR
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES_WIFI
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_SSID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_TYPE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_VOL_MODE


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 13.04.19 - 16:35
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



class DatabaseHandler (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {


    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_ENTRIES_WIFI)
        db.execSQL(SQL_CREATE_ENTRIES_CALENDAR)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }


    fun getAllSchedules(): ArrayList<ScheduleObject> {
        val db = readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf<String>(
            SCHEDULE_ID,
            SCHEDULE_NAME,
            SCHEDULE_START,
            SCHEDULE_END,
            SCHEDULE_SETTING,
            SCHEDULE_MON,
            SCHEDULE_TUE,
            SCHEDULE_WED,
            SCHEDULE_THU,
            SCHEDULE_FRI,
            SCHEDULE_SAT,
            SCHEDULE_SUN
        )

        // Filter results WHERE "title" = 'My Title'
        val selection = ""
        val selectionArgs = arrayOf<String>()

        // How you want the results sorted in the resulting Cursor
        val sortOrder = SCHEDULE_ID + " ASC"

        val cursor = db.query(
            SCHEDULE_TABLE, // The table to query
            projection, // The array of columns to return (pass null to get all)
            selection, // The columns for the WHERE clause
            selectionArgs, // don't group the rows
            null, null, // don't filter by row groups
            sortOrder                                   // The sort order
        )// The values for the WHERE clause
        val results = arrayListOf<ScheduleObject>()
        while (cursor.moveToNext()) {
            val so = ScheduleObject(
                cursor.getString(1),
                cursor.getLong(2),
                cursor.getLong(3),
                cursor.getInt(4),
                cursor.getLong(0),
                intToBool(cursor.getInt(5)),
                intToBool(cursor.getInt(6)),
                intToBool(cursor.getInt(7)),
                intToBool(cursor.getInt(8)),
                intToBool(cursor.getInt(9)),
                intToBool(cursor.getInt(10)),
                intToBool(cursor.getInt(11))
            )
            results.add(so)
        }
        cursor.close()

        db.close()
        return results
    }

    fun intToBool(value: Int): Boolean{
        if(value==0){
            return false
        }
        return true
    }

    fun getScheduleByID(id: Long): ScheduleObject {
        val db = readableDatabase

        val projection = arrayOf<String>(
            SCHEDULE_ID,
            SCHEDULE_NAME,
            SCHEDULE_START,
            SCHEDULE_END,
            SCHEDULE_SETTING,
            SCHEDULE_MON,
            SCHEDULE_TUE,
            SCHEDULE_WED,
            SCHEDULE_THU,
            SCHEDULE_FRI,
            SCHEDULE_SAT,
            SCHEDULE_SUN
        )

        val selection = SCHEDULE_ID + " = ?"
        val selectionArgs = arrayOf(id.toString())

        val sortOrder = SCHEDULE_ID + " DESC"


        val cursor = db.query(
            SCHEDULE_TABLE, // The table to query
            projection, // The array of columns to return (pass null to get all)
            selection, // The columns for the WHERE clause
            selectionArgs, // don't group the rows
            null, null, // don't filter by row groups
            sortOrder                                   // The sort order
        )// The values for the WHERE clause

        val results = arrayListOf<ScheduleObject>()
        while (cursor.moveToNext()) {

            val so = ScheduleObject(
                cursor.getString(1),
                cursor.getLong(2),
                cursor.getLong(3),
                cursor.getInt(4),
                cursor.getLong(0),
                intToBool(cursor.getInt(5)),
                intToBool(cursor.getInt(6)),
                intToBool(cursor.getInt(7)),
                intToBool(cursor.getInt(8)),
                intToBool(cursor.getInt(9)),
                intToBool(cursor.getInt(10)),
                intToBool(cursor.getInt(11))
            )


            results.add(so)
        }
        cursor.close()
        return results.get(0)
    }

    /**
     * Deletes ScheduleObject with the given id
     * @param id Switch to delete
     * @return amount of rows affected
     */
    fun deleteScheduleEntry(id: Long): Int {
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = SCHEDULE_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(id.toString())

        // Issue SQL statement.
        val retcode: Int = db.delete(SCHEDULE_TABLE, selection, selectionArgs)
        db.close()

        return retcode



    }

    /**
     * Creates a switch entry
     * @param so Switch to create
     * @return id
     */
    fun createScheduleEntry(so: ScheduleObject): ScheduleObject {
        val db = writableDatabase

        val projection = arrayOf<String>(
            SCHEDULE_ID,
            SCHEDULE_NAME,
            SCHEDULE_START,
            SCHEDULE_END,
            SCHEDULE_SETTING,
            SCHEDULE_MON,
            SCHEDULE_TUE,
            SCHEDULE_WED,
            SCHEDULE_THU,
            SCHEDULE_FRI,
            SCHEDULE_SAT,
            SCHEDULE_SUN
        )


        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        //values.put(SCHEDULE_ID, so.id)
        values.put(SCHEDULE_NAME, so.name)
        values.put(SCHEDULE_START, so.time_start)
        values.put(SCHEDULE_END, so.time_end)
        values.put(SCHEDULE_SETTING, so.time_setting)
        values.put(SCHEDULE_MON, so.mon)
        values.put(SCHEDULE_TUE, so.tue)
        values.put(SCHEDULE_WED, so.wed)
        values.put(SCHEDULE_THU, so.thu)
        values.put(SCHEDULE_FRI, so.fri)
        values.put(SCHEDULE_SAT, so.sat)
        values.put(SCHEDULE_SUN, so.sun)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(SCHEDULE_TABLE, null, values)
        Log.e(APP_NAME,"Database: Create: RowID: $newRowId")

        val newObject = ScheduleObject("",0,0,0,newRowId)

        newObject.name=so.name
        newObject.time_start=so.time_start
        newObject.time_end=so.time_end
        newObject.time_setting=so.time_setting
        newObject.mon=so.mon
        newObject.tue=so.tue
        newObject.wed=so.wed
        newObject.thu=so.thu
        newObject.fri=so.fri
        newObject.sat=so.sat
        newObject.sun=so.sun


        Log.e(APP_NAME,"Database: Create: Result: ${newObject.name}")


        db.close()
        return newObject

    }

    fun updateScheduleEntry(so: ScheduleObject) {
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(SCHEDULE_ID, so.id)
        values.put(SCHEDULE_NAME, so.name)
        values.put(SCHEDULE_START, so.time_start)
        values.put(SCHEDULE_END, so.time_end)
        values.put(SCHEDULE_SETTING, so.time_setting)
        values.put(SCHEDULE_MON, so.mon)
        values.put(SCHEDULE_TUE, so.tue)
        values.put(SCHEDULE_WED, so.wed)
        values.put(SCHEDULE_THU, so.thu)
        values.put(SCHEDULE_FRI, so.fri)
        values.put(SCHEDULE_SAT, so.sat)
        values.put(SCHEDULE_SUN, so.sun)

        val idofchangedobject = arrayOf<String>(
            so.id.toString()
        )

        // Insert the new row, returning the primary key value of the new row
        db.update(
            SCHEDULE_TABLE,
            values,
            SCHEDULE_ID + " = ?",
            idofchangedobject
        )

        db.close()

    }


    fun getAllWifiEntries(): ArrayList<WifiObject> {
        val db = readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf<String>(
            WIFI_ID,
            WIFI_SSID,
            WIFI_TYPE,
            WIFI_VOL_MODE
        )

        // Filter results WHERE "title" = 'My Title'
        val selection = ""
        val selectionArgs = arrayOf<String>()

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "$WIFI_ID ASC"

        val cursor = db.query(
            WIFI_TABLE, // The table to query
            projection, // The array of columns to return (pass null to get all)
            selection, // The columns for the WHERE clause
            selectionArgs, // don't group the rows
            null, null, // don't filter by row groups
            sortOrder                                   // The sort order
        )// The values for the WHERE clause
        val results = arrayListOf<WifiObject>()
        while (cursor.moveToNext()) {
            val wo = WifiObject(
                cursor.getLong(0),
                cursor.getString(1),
                cursor.getInt(2),
                cursor.getInt(3)
            )

            results.add(wo)
        }
        cursor.close()

        db.close()
        return results
    }

    /**
     * Creates a wifi entry
     * @param wifiObject WifiObject to create
     * @return id
     */
    fun createWifiEntry(wifiObject: WifiObject): WifiObject {
        val db = writableDatabase

        val projection = arrayOf<String>(
            WIFI_ID,
            WIFI_SSID,
            WIFI_TYPE,
            WIFI_VOL_MODE
        )


        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        //values.put(SCHEDULE_ID, so.id)
        values.put(WIFI_SSID, wifiObject.ssid)
        values.put(WIFI_TYPE, wifiObject.type)
        values.put(WIFI_VOL_MODE, wifiObject.volume)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(WIFI_TABLE, null, values)
        Log.e(APP_NAME,"Database: CreateWifi: RowID: $newRowId")

        val newObject = WifiObject(newRowId,wifiObject.ssid, wifiObject.type, wifiObject.volume)

        Log.e(APP_NAME,"Database: CreateWifi: Result: ${newObject.ssid}")

        db.close()
        return newObject

    }

    /**
     * Deletes WifiEntry with the given id
     * @param id WifiEntry to delete
     * @return amount of rows affected
     */
    fun deleteWifiEntry(id: Long): Int {
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = WIFI_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(id.toString())

        // Issue SQL statement.
        val retcode: Int = db.delete(WIFI_TABLE, selection, selectionArgs)
        db.close()

        return retcode



    }


    fun getAllCalendarEntries(): ArrayList<CalendarObject> {
        val db = readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf<String>(
            CALENDAR_ID,
            CALENDAR_ANDROID_ID,
            CALENDAR_VOL_MODE
        )

        // Filter results WHERE "title" = 'My Title'
        val selection = ""
        val selectionArgs = arrayOf<String>()

        // How you want the results sorted in the resulting Cursor
        val sortOrder = "$CALENDAR_ID ASC"

        val cursor = db.query(
            CALENDAR_TABLE, // The table to query
            projection, // The array of columns to return (pass null to get all)
            selection, // The columns for the WHERE clause
            selectionArgs, // don't group the rows
            null, null, // don't filter by row groups
            sortOrder                                   // The sort order
        )// The values for the WHERE clause
        val results = arrayListOf<CalendarObject>()
        while (cursor.moveToNext()) {
            val co = CalendarObject(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getInt(2)
            )

            results.add(co)
        }

        results.add(CalendarObject(1,0,0))
        results.add(CalendarObject(2,1,1))
        results.add(CalendarObject(3,2,2))

        db.close()
        return results
    }
}