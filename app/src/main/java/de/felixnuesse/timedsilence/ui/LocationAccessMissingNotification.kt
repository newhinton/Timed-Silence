package de.felixnuesse.timedsilence.ui

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 29.04.19 - 00:13
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

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.MainActivity
import de.felixnuesse.timedsilence.services.PauseTimerService
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface

class LocationAccessMissingNotification {

    companion object {
        const val NOTIFICATION_ID = 231


        fun buildNotification(context: Context): Notification {

            var cid = "LocationAccessNotification"
            var cname = context.getString(R.string.LocationAccessNotification)


            //NotificationManager.IMPORTANCE_NONE does not update
            val chan = NotificationChannel(cid, cname, NotificationManager.IMPORTANCE_DEFAULT)

            chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

            val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)

            /*val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { notificationIntent ->
                   PendingIntent.getActivity(context, 500, notificationIntent, 0)
             }*/

            return Notification.Builder(context, cid)
                .setContentTitle(context.getString(R.string.LocationAccessNotification_TITLE))
                .setContentText(context.getString(R.string.LocationAccessNotification_CONTENT))
                .setSmallIcon(R.drawable.ic_av_timer_black_24dp)
                .setOnlyAlertOnce(true).build()
        }


    }

}