package de.felixnuesse.timedsilence.fragments.graph

import de.felixnuesse.timedsilence.handler.volume.VolumeState

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 30.11.19 - 08:13
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

class GraphBarVolumeSwitchElement(var minuteOfDay: Int, var Volume: Int, var text: String) {

    private var barlen: Float = 1.0F
    public lateinit var state: VolumeState

    fun getBarLenght(): Float{
        return minuteOfDay.toFloat().div(1440)
    }


}