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
import android.content.ContentValues
import android.database.Cursor
import android.util.Log
import androidx.room.Room
import de.felixnuesse.timedsilence.Constants.Companion.REASON_MANUALLY_SET
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.extensions.e
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.model.data.*
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.BLUETOOTH_MAC
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.BLUETOOTH_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.BLUETOOTH_VOL_MODE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_ANDROID_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_NAME
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.CALENDAR_VOL_MODE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.KEYWORD_CALENDAR
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.KEYWORD_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.KEYWORD_KEYWORD
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.KEYWORD_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.KEYWORD_VOL_MODE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.LOG_ENTRY_CONTENT
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.LOG_ENTRY_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.LOG_ENTRY_TIMESTAMP
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_MON
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_TUE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_WED
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_THU
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_FRI
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_SAT
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SCHEDULE_SUN
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES_BLUETOOTH
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES_CALENDAR
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES_KEYWORD
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_ENTRIES_WIFI
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_CREATE_LOG_ENTRY_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.SQL_UPDATE_CALENDAR_ADD_NAME
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_ID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_SSID
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_TABLE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_TYPE
import de.felixnuesse.timedsilence.model.database.DatabaseInfo.Companion.WIFI_VOL_MODE
import de.felixnuesse.timedsilence.model.database.room.AppDatabase
import java.time.DayOfWeek


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


    var cachingEnabled=true

    var cachedSchedules = CachedArrayList<ScheduleObject>()
    var cachedCalendars = CachedArrayList<CalendarObject>()
    var cachedWifi = CachedArrayList<WifiObject>()
    var cachedKeywords = CachedArrayList<KeywordObject>()
    var cachedBluetooth = CachedArrayList<BluetoothObject>()



    fun setCaching(caching: Boolean){
        cachingEnabled=caching
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
        db.execSQL(SQL_CREATE_ENTRIES_WIFI)
        db.execSQL(SQL_CREATE_ENTRIES_CALENDAR)
        db.execSQL(SQL_UPDATE_CALENDAR_ADD_NAME)
        db.execSQL(SQL_CREATE_ENTRIES_KEYWORD)
        db.execSQL(SQL_CREATE_ENTRIES_BLUETOOTH)
        db.execSQL(SQL_CREATE_LOG_ENTRY_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(SQL_DELETE_ENTRIES)
        e("Database: $oldVersion > $newVersion")

        if(oldVersion<6){
            db.execSQL(SQL_CREATE_ENTRIES_CALENDAR)
        }

        if(oldVersion<7){
            db.execSQL(SQL_UPDATE_CALENDAR_ADD_NAME)
        }

        if(oldVersion<8){
            db.execSQL(SQL_CREATE_ENTRIES_KEYWORD)
        }

        if(oldVersion<9){
            db.execSQL(SQL_CREATE_ENTRIES_BLUETOOTH)
        }

        if(oldVersion<10){
            db.execSQL(SQL_CREATE_LOG_ENTRY_TABLE)
        }
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun clean() {
        drop()
        onCreate(writableDatabase)
    }
    private fun drop() {
        val db = writableDatabase
        val tables = arrayOf(SCHEDULE_TABLE, CALENDAR_TABLE, WIFI_TABLE, KEYWORD_TABLE, BLUETOOTH_TABLE)
        val caches = arrayOf(cachedSchedules, cachedCalendars, cachedWifi, cachedKeywords, cachedBluetooth)

        for (table in tables){
            db.execSQL("DROP TABLE IF EXISTS $table")
        }

        for (cache in caches){
            cache.clear()
        }
    }

    fun intToBool(value: Int): Boolean{
        if(value==0){
            return false
        }
        return true
    }


    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()


    /*  #########################  */
    /*  ROOM DB - SCHEDULE OBJECT  */
    /*  #########################  */
    fun getAllSchedules(): ArrayList<ScheduleObject> {
        return ArrayList(db.scheduleDao().getAllSchedules())
    }

    fun getSchedulesForWeekday(weekday: DayOfWeek): ArrayList<ScheduleObject> {
        return getAllSchedules().filter { it.isValidOnWeekday(weekday) } as ArrayList<ScheduleObject>
    }

    @Deprecated("This is not cached!")
    fun getScheduleByID(id: Long): ScheduleObject {
        return db.scheduleDao().getScheduleByID(id)
    }

    fun deleteScheduleEntry(id: Long) {
        db.scheduleDao().deleteScheduleEntry(id)
    }

    fun createScheduleEntry(so: ScheduleObject): ScheduleObject {
        val id = db.scheduleDao().createScheduleEntry(so)
        return db.scheduleDao().getScheduleByID(id)

    }

    fun updateScheduleEntry(so: ScheduleObject) {
        db.scheduleDao().updateScheduleEntry(so)
    }

    /*    #############################  */
    /*  ROOM DB - SCHEDULE OBJECT - END  */
    /*    #############################  */



    fun getAllWifiEntries(): ArrayList<WifiObject> {
        if (cachedWifi.cacheInitialized && cachingEnabled) {
            return cachedWifi
        }

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
        cachedWifi.set(results)
        return cachedWifi
    }

    /**
     * Creates a wifi entry
     * @param wifiObject WifiObject to create
     * @return id
     */
    fun createWifiEntry(wifiObject: WifiObject): WifiObject {
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        //values.put(SCHEDULE_ID, so.id)
        values.put(WIFI_SSID, wifiObject.ssid)
        values.put(WIFI_TYPE, wifiObject.type)
        values.put(WIFI_VOL_MODE, wifiObject.volume)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(WIFI_TABLE, null, values)

        val newObject = WifiObject(newRowId,wifiObject.ssid, wifiObject.type, wifiObject.volume)


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

        if (cachedCalendars.cacheInitialized && cachingEnabled) {
            return cachedCalendars
        }


        val db = readableDatabase

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        val projection = arrayOf<String>(
            CALENDAR_ID,
            CALENDAR_ANDROID_ID,
            CALENDAR_VOL_MODE,
            CALENDAR_NAME
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
        )


        // The values for the WHERE clause
        val results = arrayListOf<CalendarObject>()
        while (cursor.moveToNext()) {
            val co = CalendarObject(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getInt(2)
            )

            if(cursor.getString(3)!=null){
                if(cursor.getString(3) != ""){
                    co.name=cursor.getString(3);
                }
            }

            results.add(co)
        }

        cursor.close()
        db.close()
        cachedCalendars.set(results)
        return cachedCalendars
    }

    /**
     * Deletes CalendarEntry with the given id
     * @param id WifiEntry to delete
     * @return amount of rows affected
     */
    fun deleteCalendarEntry(id: Long): Int {
        val db = writableDatabase
        // Define 'where' part of query.
        val selection = CALENDAR_ID + " LIKE ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(id.toString())

        // Issue SQL statement.
        val retcode: Int = db.delete(CALENDAR_TABLE, selection, selectionArgs)
        db.close()

        return retcode
    }
    /**
     * Creates a calendar entry
     * @param wifiObject WifiObject to create
     * @return id
     */
    fun createCalendarEntry(calendarObject: CalendarObject): CalendarObject {
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(CALENDAR_ANDROID_ID, calendarObject.externalID)
        values.put(CALENDAR_VOL_MODE, calendarObject.volume)
        if(!calendarObject.name.equals("NOTSET")){
            values.put(CALENDAR_NAME, calendarObject.name)
        }


        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(CALENDAR_TABLE, null, values)

        val newObject = CalendarObject(newRowId,calendarObject.externalID, calendarObject.volume)
        if(!calendarObject.name.equals("NOTSET")){
            newObject.name=calendarObject.name
        }
        db.close()
        return newObject

    }

    fun updateCalendarEntry(co: CalendarObject) {
        val db = writableDatabase

        // Create a new map of values, where column names are the keys
        val values = ContentValues()
        values.put(CALENDAR_ANDROID_ID, co.externalID)
        values.put(CALENDAR_VOL_MODE, co.volume)
        if(!co.name.equals("NOTSET")){
            values.put(CALENDAR_NAME, co.name)
        }

        val idofchangedobject = arrayOf<String>(
            co.id.toString()
        )

        // Insert the new row, returning the primary key value of the new row
        db.update(
            CALENDAR_TABLE,
            values,
            CALENDAR_ID + " = ?",
            idofchangedobject
        )

        db.close()

    }

    @Deprecated("This is inefficient. Use getAllCalendarEntries and cache in a map!")
    fun getCalendarEntryByExtId(extID:String): CalendarObject? {
        for(elem in this.getAllCalendarEntries()){
            if(elem.externalID.toString().equals(extID)){
                return elem
            }
        }
        return null
    }


    fun getKeywordProjection(): Array<String> {
        return arrayOf(
            KEYWORD_ID,
            KEYWORD_CALENDAR,
            KEYWORD_KEYWORD,
            KEYWORD_VOL_MODE
        )
    }

    fun getKeywordListFromCursor(cursor: Cursor): ArrayList<KeywordObject> {
        val results = arrayListOf<KeywordObject>()
        while (cursor.moveToNext()) {
            val cko = KeywordObject(
                cursor.getLong(0),
                cursor.getLong(1),
                cursor.getString(2),
                cursor.getInt(3)
            )
            results.add(cko)
        }
        cursor.close()
        return results
    }

    fun getKeywords(): ArrayList<KeywordObject> {
        if (cachedKeywords.cacheInitialized && cachingEnabled) {
            return cachedKeywords
        }

        val db = readableDatabase
        val cursor = db.query(
            KEYWORD_TABLE,
            getKeywordProjection(),
            "",
            arrayOf<String>(),
            null, null,
            "$KEYWORD_ID ASC"
        )
        cachedKeywords.set(getKeywordListFromCursor(cursor))
        db.close()
        return cachedKeywords
    }

    fun createKeyword(keywordObject: KeywordObject): KeywordObject {
        val db = writableDatabase

        val values = ContentValues()
        values.put(KEYWORD_CALENDAR, keywordObject.calendarid)
        values.put(KEYWORD_KEYWORD, keywordObject.keyword)
        values.put(KEYWORD_VOL_MODE, keywordObject.volume)

        // Insert the new row, returning the primary key value of the new row
        val newRowId = db.insert(KEYWORD_TABLE, null, values)
        val newObject = KeywordObject(newRowId, keywordObject.calendarid,  keywordObject.keyword,  keywordObject.volume)
        db.close()
        return newObject
    }

    fun editKeyword(keywordObject: KeywordObject){
        val db = writableDatabase

        val values = ContentValues()
        values.put(KEYWORD_ID, keywordObject.id)
        values.put(KEYWORD_CALENDAR, keywordObject.calendarid)
        values.put(KEYWORD_KEYWORD, keywordObject.keyword)
        values.put(KEYWORD_VOL_MODE, keywordObject.volume)

        val idofchangedobject = arrayOf<String>(
            keywordObject.id.toString()
        )

        db.update(
            KEYWORD_TABLE,
            values,
            KEYWORD_ID + " = ?",
            idofchangedobject
        )
        db.close()
    }

    fun deleteKeyword(id: Long): Int {
        val db = writableDatabase
        val selection = KEYWORD_ID + " LIKE ?"
        val selectionArgs = arrayOf(id.toString())
        val retcode: Int = db.delete(KEYWORD_TABLE, selection, selectionArgs)
        db.close()
        return retcode
    }

    fun getBluetoothEntries(): ArrayList<BluetoothObject> {
        if (cachedBluetooth.cacheInitialized && cachingEnabled) {
            return cachedBluetooth
        }

        val db = readableDatabase
        val cursor = db.query(
            BLUETOOTH_TABLE,
            arrayOf(
                BLUETOOTH_MAC,
                BLUETOOTH_VOL_MODE
            ),
            "",
            arrayOf<String>(),
            null, null,
            "$BLUETOOTH_MAC ASC"
        )
        cachedBluetooth.set(getBluetoothObjectFromCursor(cursor))
        db.close()
        return cachedBluetooth
    }

    private fun getBluetoothObjectFromCursor(cursor: Cursor): ArrayList<BluetoothObject> {
        val results = arrayListOf<BluetoothObject>()
        while (cursor.moveToNext()) {
            val cko = BluetoothObject(
                "",
                cursor.getString(0)
            )
            cko.volumeState = cursor.getInt(1)
            results.add(cko)
        }
        cursor.close()
        return results
    }

    fun addOrUpdateBluetooth(bluetoothObject: BluetoothObject){
        val db = writableDatabase

        val values = ContentValues()
        values.put(BLUETOOTH_MAC, bluetoothObject.address)
        values.put(BLUETOOTH_VOL_MODE, bluetoothObject.volumeState)

        db.insertWithOnConflict(
            BLUETOOTH_TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        db.close()
    }

    fun deleteBluetoothDevice(macadress: String): Int {
        Log.e(TAG(), "delete: $macadress")

        val db = writableDatabase
        val selection = "$BLUETOOTH_MAC LIKE ?"
        val selectionArgs = arrayOf(macadress)
        val retcode: Int = db.delete(BLUETOOTH_TABLE, selection, selectionArgs)
        db.close()
        return retcode
    }


    fun getLogEntries(): ArrayList<VolumeState> {

        val db = readableDatabase
        val cursor = db.query(
            LOG_ENTRY_TABLE,
            arrayOf(
                LOG_ENTRY_TIMESTAMP,
                LOG_ENTRY_CONTENT
            ),
            "",
            arrayOf<String>(),
            null, null,
            "$LOG_ENTRY_TIMESTAMP DESC"
        )

        val results = arrayListOf<VolumeState>()
        /*while (cursor.moveToNext()) {
            val cko = BluetoothObject(
                "",
                cursor.getString(0)
            )
            cko.volumeState = cursor.getInt(1)
            results.add(cko)
        }*/

        val state = VolumeState(2)
        state.setReason(REASON_MANUALLY_SET, "main reason.")

        results.add(state)
        cursor.close()
        return results
    }

    fun addLogEntry(state: VolumeState){
    }

}