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
        const val DATABASE_VERSION = 4
        const val DATABASE_NAME = "TimedSilence.db"

        val SCHEDULE_TABLE="timetable"
        val SCHEDULE_ID= "id"
        val SCHEDULE_START= "time_start"
        val SCHEDULE_END= "time_end"
        val SCHEDULE_SETTING= "schedule_volume"
        val SCHEDULE_NAME= "schedule_name"

        val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${SCHEDULE_TABLE} (" +
                    "${SCHEDULE_ID} INTEGER PRIMARY KEY," +
                    "${SCHEDULE_START} LONG," +
                    "${SCHEDULE_END} LONG," +
                    "${SCHEDULE_SETTING} INT," +
                    "${SCHEDULE_NAME} TEXT)"


        val WIFI_TABLE="wifi_timetable"
        val WIFI_ID= "wifi_id"
        val WIFI_TYPE= "wifi_type"
        val WIFI_VOL_MODE= "wifi_volume_mode"
        val WIFI_SSID= "wifi_ssid"

        val SQL_CREATE_ENTRIES_WIFI =
            "CREATE TABLE ${WIFI_TABLE} (" +
                    "${WIFI_ID} INTEGER PRIMARY KEY," +
                    "${WIFI_SSID} TEXT," +
                    "${WIFI_TYPE} INT," +
                    "${WIFI_VOL_MODE} INT)"

    }
}