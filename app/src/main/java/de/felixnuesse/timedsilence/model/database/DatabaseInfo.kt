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
        val TABLE="timetable";
        val SCHEDULE_ID= "id";
        val SCHEDULE_START= "time_start";
        val SCHEDULE_END= "time_end";
        val SCHEDULE_SETTING= "schedule_volume";
        val SCHEDULE_NAME= "schedule_name";

        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "FeedReader.db"

        val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${TABLE} (" +
                    "${SCHEDULE_ID} INTEGER PRIMARY KEY," +
                    "${SCHEDULE_START} LONG," +
                    "${SCHEDULE_END} LONG," +
                    "${SCHEDULE_SETTING} INT," +
                    "${SCHEDULE_NAME} TEXT)"



    }
}