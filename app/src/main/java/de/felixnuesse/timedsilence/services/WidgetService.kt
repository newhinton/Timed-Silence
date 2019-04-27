package de.felixnuesse.timedsilence.services

import android.app.Service
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.support.v4.view.accessibility.AccessibilityEventCompat.setAction
import android.content.Intent
import android.os.IBinder
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import de.felixnuesse.timedsilence.widgets.AHourWidget


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 27.04.19 - 13:10
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
class WidgetService : Service(), TimerInterface {
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun timerStarted(timeAsLong: Long) {
        setTimeOnWidgets(timeAsLong)
    }

    override fun timerReduced(timeAsLong: Long) {
        setTimeOnWidgets(timeAsLong)
    }

    override fun timerFinished() {

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        Log.e(Constants.APP_NAME,"WidgetService: Started and registered service!")
        PauseTimerService.registerListener(this)

        return super.onStartCommand(intent, flags, startId)
    }

    fun setTimeOnWidgets(timeAsLong: Long){


        val intent = Intent(this, AHourWidget::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        intent.putExtra("test_widget",timeAsLong)



        Log.e(Constants.APP_NAME,"WidgetService: Updated Widgets!")

        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:

        val awm= AppWidgetManager.getInstance(application)
        val comname=ComponentName(application, AHourWidget::class.java)
        val ids = awm.getAppWidgetIds(comname)
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)

    }


}