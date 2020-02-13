package de.felixnuesse.timedsilence

import java.text.SimpleDateFormat
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

class Utils{

    companion object{

        fun getDate(milliSeconds: Long): String {
            return getDate(milliSeconds.toString())
        }

        fun getDate(milliSeconds: String): String {
            val formatter = SimpleDateFormat.getDateInstance()
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds.toLong()
            return formatter.format(calendar.time)
        }

    }

}