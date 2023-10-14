package de.felixnuesse.timedsilence.ui.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME


/**
 * Copyright (C) 2023  Felix Nüsse
 * Created on 14.10.23 - 16:57
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

class ErrorNotifications {

    companion object{
        const val NOTIFICATION_ID=11211
        const val ERROR_CHANNEL_ID="Errors"
        const val ERROR_CHANNEL_NAME="This channel will only be used to show erros in this app."

        fun cancelNotification(context: Context) {
            Log.e(APP_NAME, "ErrorNotifications: Cancel Notification")
            var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID)
        }

    }

    fun showError(context: Context, title: String, content: String) {

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(ERROR_CHANNEL_ID, ERROR_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notification = Notification.Builder(context, ERROR_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_error_black_24dp)
            .setOnlyAlertOnce(true)

        notificationManager.notify(
            NOTIFICATION_ID,
            notification.build()
        )
    }
}