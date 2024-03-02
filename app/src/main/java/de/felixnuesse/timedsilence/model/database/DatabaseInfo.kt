package de.felixnuesse.timedsilence.model.database

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 13.04.19 - 19:34
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

class DatabaseInfo{
    companion object {

        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 9
        const val DATABASE_NAME = "TimedSilence.db"

        val SCHEDULE_TABLE="timetable"
        val SCHEDULE_ID= "id"
        val SCHEDULE_START= "time_start"
        val SCHEDULE_END= "time_end"
        val SCHEDULE_SETTING= "schedule_volume"
        val SCHEDULE_NAME= "schedule_name"
        val SCHEDULE_MON= "schedule_active_mon"
        val SCHEDULE_TUE= "schedule_active_tue"
        val SCHEDULE_WED= "schedule_active_wed"
        val SCHEDULE_THU= "schedule_active_thu"
        val SCHEDULE_FRI= "schedule_active_fri"
        val SCHEDULE_SAT= "schedule_active_sat"
        val SCHEDULE_SUN= "schedule_active_sun"

        val SQL_CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS ${SCHEDULE_TABLE} (" +
                    "${SCHEDULE_ID} INTEGER PRIMARY KEY," +
                    "${SCHEDULE_START} LONG," +
                    "${SCHEDULE_END} LONG," +
                    "${SCHEDULE_SETTING} INT," +
                    "${SCHEDULE_MON} INT DEFAULT 0," +
                    "${SCHEDULE_TUE} INT DEFAULT 0," +
                    "${SCHEDULE_WED} INT DEFAULT 0," +
                    "${SCHEDULE_THU} INT DEFAULT 0," +
                    "${SCHEDULE_FRI} INT DEFAULT 0," +
                    "${SCHEDULE_SAT} INT DEFAULT 0," +
                    "${SCHEDULE_SUN} INT DEFAULT 0," +
                    "${SCHEDULE_NAME} TEXT)"


        val WIFI_TABLE="wifi_timetable"
        val WIFI_ID= "wifi_id"
        val WIFI_TYPE= "wifi_type"
        val WIFI_VOL_MODE= "wifi_volume_mode"
        val WIFI_SSID= "wifi_ssid"

        val SQL_CREATE_ENTRIES_WIFI =
            "CREATE TABLE IF NOT EXISTS ${WIFI_TABLE} (" +
                    "${WIFI_ID} INTEGER PRIMARY KEY," +
                    "${WIFI_SSID} TEXT," +
                    "${WIFI_TYPE} INT," +
                    "${WIFI_VOL_MODE} INT)"

        val CALENDAR_TABLE="calendar_timetable"
        val CALENDAR_ID= "calendar_id"
        val CALENDAR_ANDROID_ID= "calendar_android_id"
        val CALENDAR_VOL_MODE= "calendar_volume_mode"
        val CALENDAR_NAME= "calendar_name"

        val SQL_CREATE_ENTRIES_CALENDAR =
            "CREATE TABLE IF NOT EXISTS ${CALENDAR_TABLE} (" +
                    "${CALENDAR_ID} INTEGER PRIMARY KEY," +
                    "${CALENDAR_ANDROID_ID} INTEGER," +
                    "${CALENDAR_VOL_MODE} INT)"

        val SQL_UPDATE_CALENDAR_ADD_NAME = "ALTER TABLE ${CALENDAR_TABLE} ADD COLUMN ${CALENDAR_NAME} TEXT"


        val KEYWORD_TABLE="KEYWORD_TABLE"
        val KEYWORD_ID= "KEYWORD_ID"
        val KEYWORD_VOL_MODE= "KEYWORD_VOL_MODE"
        val KEYWORD_KEYWORD= "KEYWORD_KEYWORD"
        val KEYWORD_CALENDAR= "KEYWORD_CALENDAR"

        val SQL_CREATE_ENTRIES_KEYWORD =
            "CREATE TABLE IF NOT EXISTS $KEYWORD_TABLE (" +
                    "$KEYWORD_ID INTEGER PRIMARY KEY," +
                    "$KEYWORD_KEYWORD TEXT," +
                    "$KEYWORD_CALENDAR INT," +
                    "$KEYWORD_VOL_MODE INT)"


        val BLUETOOTH_TABLE="BLUETOOTH_TABLE"
        val BLUETOOTH_MAC= "BLUETOOTH_MAC"
        val BLUETOOTH_VOL_MODE= "BLUETOOTH_VOL_MODE"

        val SQL_CREATE_ENTRIES_BLUETOOTH =
            "CREATE TABLE IF NOT EXISTS $BLUETOOTH_TABLE (" +
                    "$BLUETOOTH_MAC TEXT PRIMARY KEY," +
                    "$BLUETOOTH_VOL_MODE INT)"
    }
}