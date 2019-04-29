package de.felixnuesse.timedsilence.ui

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.MainActivity
import de.felixnuesse.timedsilence.services.PauseTimerService
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface





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

class PauseNotification: TimerInterface{

    lateinit var mContext: Context

    override fun timerStarted(timeAsLong: Long) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun timerReduced(timeAsLong: Long) {
        Log.e(APP_NAME, "test")
        val n = startNotification(mContext, "time", PauseTimerService.getTimestampInProperLength(timeAsLong))

        NotificationManagerCompat.from(mContext).notify(11211, n)


    }

    override fun timerFinished() {
        cancelNotification(11211)
    }


    fun cancelNotification(notifyId: Int) {
        NotificationManagerCompat.from(mContext).cancel(notifyId)
    }

    fun startNotification(context: Context): Notification {
        return startNotification(context, "title", "content")
    }

    fun startNotification(context: Context, title: String, content: String): Notification {


        PauseTimerService.registerListener(this)
        mContext=context


        var cid= "my_service"
        var cname="My Background Service"

        val chan = NotificationChannel(cid, cname, NotificationManager.IMPORTANCE_NONE)

        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)

        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(context, 500, notificationIntent, 0)
        }

        val notification: Notification = Notification.Builder(context, cid)
            .setContentTitle("Title")
            .setContentText("ContentText")
            .setSmallIcon(R.drawable.ic_add_circle_outline_black_24dp)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true)
            .build()

        return notification
    }

}