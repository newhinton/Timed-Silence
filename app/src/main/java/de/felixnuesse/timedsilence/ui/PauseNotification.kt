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
import de.felixnuesse.timedsilence.Constants
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


    companion object{
        const val NOTIFICATION_ID=11211
    }

    var mNotfificationTitle="Paused for:"


    override fun timerStarted(context: Context, timeAsLong: Long, timeAsString: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun timerReduced(context: Context, timeAsLong: Long, timeAsString: String) {

        var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, buildNotification(context, mNotfificationTitle, PauseTimerService.getTimestampInProperLength(timeAsLong)).build())


    }

    override fun timerFinished(context: Context) {
    }


    fun cancelNotification(notifyId: Int, context: Context) {

        Log.e(APP_NAME, "PauseNotification: Cancel Notification")

        var notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notifyId)


    }

    fun buildNotification(context: Context, title: String, content: String):Notification.Builder{

        var cid= "my_service"
        var cname="My Background Service"


        //NotificationManager.IMPORTANCE_NONE does not update
        val chan = NotificationChannel(cid, cname, NotificationManager.IMPORTANCE_LOW)

        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE

        val service = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)

     /*   val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java).let { notificationIntent ->
            PendingIntent.getActivity(context, 500, notificationIntent, 0)
        }*/

        return Notification.Builder(context, cid)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_av_timer_black_24dp)
            .setOnlyAlertOnce(true)
    }

    fun startNotification(context: Context): Notification {
        return startNotification(context, mNotfificationTitle, "")
    }

    fun startNotification(context: Context, content: String): Notification {
        PauseTimerService.registerListener(this)
        return buildNotification(context, mNotfificationTitle, content).build()
    }

    fun startNotification(context: Context, title: String, content: String): Notification {
        PauseTimerService.registerListener(this)
        return buildNotification(context, title, content).build()
    }





}