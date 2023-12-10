package de.felixnuesse.timedsilence.ui.notifications

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
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.trigger.Trigger

class PausedNotification : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.e(TAG, "PausedNotification: Recieved Intent!")

        val action = intent?.action
        if(action == ACTION_END_PAUSE_AND_CHECK){
            context?.let {
                // Set Volume now.
                //VolumeCalculator(it).calculateAllAndApply()
            }
        }

        if(action == ACTION_END_PAUSE || action == ACTION_END_PAUSE_AND_CHECK){
            context?.let {
                Trigger(it).createTimecheck()
                Trigger(it).checkIfNextAlarmExists()
            }
        }
    }

    companion object {

        private const val TAG = "PausedNotification"
        private const val ACTION_END_PAUSE = "ACTION_END_PAUSE"
        private const val ACTION_END_PAUSE_AND_CHECK = "ACTION_END_PAUSE_CHECK"
        private const val NOTIFICATION_ID = 498

        fun show(context: Context){
            Log.e(TAG, "PausedNotification: Show Notification")
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if(PreferencesManager(context).shouldShowNotification()){
                notificationManager.notify(NOTIFICATION_ID, buildNotification(context))
            }
        }

        fun buildNotification(context: Context): Notification {

            val cid = "PausedNotification"
            val cname = context.getString(R.string.PausedNotification)

            //NotificationManager.IMPORTANCE_NONE does not update
            val chan = NotificationChannel(cid, cname, NotificationManager.IMPORTANCE_LOW)

            chan.lockscreenVisibility = Notification.VISIBILITY_SECRET

            val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)


            val checkIntent = Intent(context, PausedNotification::class.java).apply {
                action = ACTION_END_PAUSE_AND_CHECK
            }

            val checkPendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, checkIntent, PendingIntent.FLAG_IMMUTABLE)
            val resumeAndCheckAction = NotificationCompat.Action.Builder(
                0,
                context.getString(R.string.PausedNotification_RESUME_AND_CHECK),
                checkPendingIntent
            ).build()


            val snoozeIntent = Intent(context, PausedNotification::class.java).apply {
                action = ACTION_END_PAUSE
            }
            val snoozePendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE)
            val resumeAction = NotificationCompat.Action.Builder(
                0,
                context.getString(R.string.PausedNotification_RESUME),
                snoozePendingIntent
            ).build()

            return NotificationCompat.Builder(context, cid)
                .setContentTitle(context.getString(R.string.PausedNotification_TITLE))
                .setContentText(context.getString(R.string.PausedNotification_CONTENT))
                .setSmallIcon(R.drawable.logo_pause)
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .addAction(resumeAction)
                .addAction(resumeAndCheckAction)
                .build()
        }

        fun cancelNotification(context: Context) {
            Log.e(TAG, "PausedNotification: Cancel Notification")
            var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(NOTIFICATION_ID)
        }

    }

}