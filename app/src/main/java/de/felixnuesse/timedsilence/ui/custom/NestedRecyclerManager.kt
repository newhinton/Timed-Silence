package de.felixnuesse.timedsilence.ui.custom

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager

/**
 * Copyright (C) 2020  Felix Nüsse
 * Created on 13.02.20 - 15:28
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
class NestedRecyclerManager(val context: Context): LinearLayoutManager(context) {

    private var isScrollEnabled = false

    fun setScrollEnabled(flag: Boolean) {
        isScrollEnabled = flag
    }
    override fun canScrollVertically(): Boolean { //Similarly you can customize "canScrollHorizontally()" for managing horizontal scroll
        return isScrollEnabled && super.canScrollVertically()
    }
}