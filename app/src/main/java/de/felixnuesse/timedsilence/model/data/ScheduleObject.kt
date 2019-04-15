package de.felixnuesse.timedsilence.model.data

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 13.04.19 - 19:41
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
class ScheduleObject(name: String, time_start: Long, time_end: Long, time_setting: Int, id: Int) {

    var name: String = name
    var time_start: Long = time_start
    var time_end: Long = time_end
    var time_setting: Int = time_setting
    var id: Int = id



}