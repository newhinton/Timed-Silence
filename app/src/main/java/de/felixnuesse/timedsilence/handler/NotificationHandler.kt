package de.felixnuesse.timedsilence.handler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.MainActivity
import de.felixnuesse.timedsilence.R


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 27.01.21 - 10:45
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 * timed-silence
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
class NotificationHandler {

    var CHANNEL_ID_CALENDAR_ERROR = "CHANNEL_ID_CALENDAR_ERROR"

    private fun createNotificationChannel(c: Context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = c.getString(R.string.channel_name_calendar_error)
            val descriptionText = c.getString(R.string.channel_description_calendar_error)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_CALENDAR_ERROR, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = c.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showNotification(c: Context){
        createNotificationChannel(c)
        var builder = NotificationCompat.Builder(c, CHANNEL_ID_CALENDAR_ERROR)
            .setSmallIcon(R.drawable.ic_schedule_appicon)
            .setContentTitle("⚠ Calendar Issue")
            .setContentText("Your calendarnames may have changed!")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Please check if you need to edit the settings in the app to make sure to not be interrupted to unopportune times!")
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)


        val notificationIntent = Intent(c, MainActivity::class.java)
        notificationIntent.putExtra(Constants.MAIN_ACTIVITY_LOAD_CALENDAR, Constants.MAIN_ACTIVITY_LOAD_CALENDAR_FORCE)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val intent = PendingIntent.getActivity(
            c, 0,
            notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT
        )


        builder.setContentIntent(intent)

        with(NotificationManagerCompat.from(c)) {
            // notificationId is a unique int for each notification that you must define
            notify(5614468+Math.random().toInt(), builder.build())
        }

    }
}