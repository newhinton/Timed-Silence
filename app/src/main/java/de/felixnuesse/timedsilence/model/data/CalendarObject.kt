package de.felixnuesse.timedsilence.model.data;

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 07.11.19 - 11:34
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

class CalendarObject(id: Long, ext_id: Long, volume: Int) {

        var id: Long = id
        var ext_id: Long = ext_id
        var volume: Int = volume
        var name: String = "NOTSET"
        var color: Int = 0


}