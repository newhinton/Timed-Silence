package de.felixnuesse.timedsilence.model.data;

/**
 * Copyright (C) 2021  Felix Nüsse
 * Created on 15.07.21 - 20:20
 * <p>
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 * <p>
 * <p>
 * This program is released under the GPLv3 license
 * <p>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */

class KeywordObject(id: Long, calendarid: Long, keyword: String, volume: Int) {

        companion object {
                const val ALL_CALENDAR: Long = -1
        }
        var id: Long = id
        var calendarid: Long = calendarid
        var keyword: String = keyword
        var volume: Int = volume


}