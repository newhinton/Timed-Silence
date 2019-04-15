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
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.TABLE
import de.felixnuesse.timedsilence.model.data.ScheduleObject
import android.content.ContentValues
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME


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


    fun getAllSchedules(): ArrayList<ScheduleObject> {
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
                cursor.getLong(0)
            )

            results.add(so)
        }
        cursor.close()

        return results
    }


    fun getScheduleByID(id: Long): ScheduleObject {
        val db = readableDatabase

        val projection = arrayOf<String>(
            SCHEDULE_ID,
            SCHEDULE_NAME,
            SCHEDULE_START,
            SCHEDULE_END,
            SCHEDULE_SETTING
        )

        val selection = SCHEDULE_ID + " = ?"
        val selectionArgs = arrayOf(id.toString())

        val sortOrder = SCHEDULE_ID + " DESC"


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
                cursor.getLong(0)
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
    fun deleteEntry(id: Long): Int {
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = SCHEDULE_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(id.toString())
        // Issue SQL statement.
        return db.delete(TABLE, selection, selectionArgs)

    }

    /**
     * Creates a switch entry
     * @param so Switch to create
     * @return id
     */
    fun createEntry(so: ScheduleObject): ScheduleObject {
        val db = writableDatabase

        val projection = arrayOf<String>(
            SCHEDULE_ID,
            SCHEDULE_NAME,
            SCHEDULE_START,
            SCHEDULE_END,
            SCHEDULE_SETTING
        )


        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        //values.put(SCHEDULE_ID, so.id)
        values.put(SCHEDULE_NAME, so.name)
        values.put(SCHEDULE_START, so.time_start)
        values.put(SCHEDULE_END, so.time_end)
        values.put(SCHEDULE_SETTING, so.time_setting)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(TABLE, null, values)
        Log.e(APP_NAME,"Database: Create: RowID: $newRowId")

        val newObject = ScheduleObject("",0,0,0,newRowId)

        newObject.name=so.name
        newObject.time_start=so.time_start
        newObject.time_end=so.time_end
        newObject.time_setting=so.time_setting


        Log.e(APP_NAME,"Database: Create: Result: ${newObject.name}")

        return newObject

    }

    fun updateEntry(so: ScheduleObject) {
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(SCHEDULE_ID, so.id)
        values.put(SCHEDULE_NAME, so.name)
        values.put(SCHEDULE_START, so.time_start)
        values.put(SCHEDULE_END, so.time_end)
        values.put(SCHEDULE_SETTING, so.time_setting)

        val idofchangedobject = arrayOf<String>(
            so.id.toString()
        )

        // Insert the new row, returning the primary key value of the new row
        db.update(
            TABLE,
            values,
            SCHEDULE_ID + " = ?",
            idofchangedobject
        )

    }


}