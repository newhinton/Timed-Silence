package de.felixnuesse.timedsilence.widgets

import android.content.Context
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.services.PauseTimerService

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 27.04.19 - 16:42
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

class AutoHourWidget: AbstractHourWidget(){
    override val mWidgetTime: Long = 0 //can be ignored in this class
    override val mWidgetName: String = "AutoTimeWidget"
    override val mWidgetClass: Class<*> = AutoHourWidget::class.java

    override fun toggleAction(context: Context){
        PauseTimerService.startAutoTimer(context)
    }

    override fun getDefaultButtonText(context: Context):String{
        return context.getString(R.string.pause_check_schedule)
    }

    override fun buttonTextDecider():Boolean{
        return PauseTimerService.isTimerRunning()
    }
}