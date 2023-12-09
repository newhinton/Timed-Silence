package de.felixnuesse.timedsilence.util

import java.text.SimpleDateFormat
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.Temporal
import java.util.*


/**
 * Copyright (C) 2020  Felix Nüsse
 * Created on 11.02.20 - 16:28
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

class DateUtil{

    companion object{

        fun getDate(milliSeconds: Long, format: String): String {
            val formatter = SimpleDateFormat(format, Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

        fun getDate(milliSeconds: Long): String {
           return getDate(milliSeconds, "dd.MM.yyyy HH:mm:ss")
        }

        fun getDate(milliSeconds: String): String {
            return getDate(milliSeconds.toLong())
        }

        fun getDate(): String {
            return getDate(System.currentTimeMillis())
        }

        fun getDateFormatted(format: String): String {
            return getDate(System.currentTimeMillis(), format)
        }

        fun getDelta(startMillis: Long, endMillis: Long): Duration {
            val start = LocalDateTime.ofInstant(Instant.ofEpochMilli(startMillis), ZoneId.systemDefault())
            val end = LocalDateTime.ofInstant(Instant.ofEpochMilli(endMillis), ZoneId.systemDefault())
            return Duration.between(start, end)
        }

        fun getMinOffsetHumanReadable(offset: Long): String {
            return "${offset/60}H:${offset % 60}M"
        }

        fun getMidnight(): LocalDateTime {
            val midnight: LocalTime = LocalTime.MIDNIGHT
            val today: LocalDate = LocalDate.now(ZoneId.systemDefault())
            return LocalDateTime.of(today, midnight)
        }
    }

}