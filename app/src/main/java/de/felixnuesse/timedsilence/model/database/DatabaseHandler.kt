package de.felixnuesse.timedsilence.model.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.DATABASE_NAME
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.DATABASE_VERSION
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_END
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_NAME
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_SETTING
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_START
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.TABLE
import de.felixnuesse.timedsilence.model.data.ScheduleObject

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


    fun getAllSchedules(): List<ScheduleObject> {
        val db = readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf<String>(
            SCHEDULE_ID,
            SCHEDULE_NAME,
            SCHEDULE_START,
            SCHEDULE_END,
            SCHEDULE_SETTING
        )

        // Filter results WHERE "title" = 'My Title'
        val selection = ""
        val selectionArgs = arrayOf<String>()

        // How you want the results sorted in the resulting Cursor
        val sortOrder = SCHEDULE_ID + " ASC"

        val cursor = db.query(
            TABLE, // The table to query
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
                cursor.getInt(0)
            )

            results.add(so)
        }
        cursor.close()





        results.clear()
        results.add(ScheduleObject("Early morning", 0, 8*60*60*1000, Constants.TIME_SETTING_SILENT, 1))
        results.add(ScheduleObject("Work", 8*60*60*1000+1, 16*60*60*1000, Constants.TIME_SETTING_VIBRATE, 2))
        results.add(ScheduleObject("Free Time", 16*60*60*1000+1, 22*60*60*1000, Constants.TIME_SETTING_LOUD, 3))
        results.add(ScheduleObject("Evening", 22*60*60*1000+1, 24*60*60*1000, Constants.TIME_SETTING_SILENT, 4))
        return results
    }

}