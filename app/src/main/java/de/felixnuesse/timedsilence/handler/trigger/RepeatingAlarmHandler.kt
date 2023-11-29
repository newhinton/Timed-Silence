package de.felixnuesse.timedsilence.handler.trigger

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.receiver.AlarmBroadcastReceiver
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 10.04.19 - 12:00
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


class RepeatingAlarmHandler(override var mContext: Context) : TriggerInterface {

    companion object {
        private const val TAG = "RepeatingAlarmHandler"
    }

    override fun createTimecheck() {
        val interval =
            SharedPreferencesHandler.getPref(
                mContext,
                PrefConstants.PREF_INTERVAL_CHECK,
                PrefConstants.PREF_INTERVAL_CHECK_DEFAULT
            )
        createRepeatingTimecheck(interval)
    }


    fun createRepeatingTimecheck(intervalInMinutes: Int) {

        Log.d(TAG, "AlarmHandler: CreateRepeatingTimecheck: Precreate")
        //todo create inexact version
        val alarms = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarms.setRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + 100,
            (1000 * 60 * intervalInMinutes).toLong(),
            createBroadcast(0L)
        )
    }

    override fun removeTimecheck() {

        val alarms = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarms.cancel(createBroadcast(0L))
        createBroadcast(0L).cancel()

        if (!checkIfNextAlarmExists()) {
            Log.d(TAG, "AlarmHandler: Recurring alarm canceled")
            return
        }
        Log.e(TAG, "AlarmHandler: Error canceling recurring alarm!")

    }

    override fun createBroadcast(flag: Int, targettime: Long): PendingIntent {

        val broadcastIntent = Intent(mContext, AlarmBroadcastReceiver::class.java)
        broadcastIntent.putExtra(
            Constants.BROADCAST_INTENT_ACTION,
            Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME
        )

        // The Pending Intent to pass in AlarmManager
        return PendingIntent.getBroadcast(
            mContext,
            Constants.RECURRING_INTENT_ID, broadcastIntent, flag or FLAG_IMMUTABLE
        )

    }

    override fun createBroadcast(targettime: Long): PendingIntent {

        val broadcastIntent = Intent(mContext, AlarmBroadcastReceiver::class.java)
        broadcastIntent.putExtra(
            Constants.BROADCAST_INTENT_ACTION,
            Constants.BROADCAST_INTENT_ACTION_DELAY
        )
        broadcastIntent.putExtra(
            Constants.BROADCAST_INTENT_ACTION_DELAY_EXTRA,
            Constants.BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW
        )
        broadcastIntent.putExtra(
            Constants.BROADCAST_INTENT_ACTION_TARGET_TIME,
            targettime
        )

        // The Pending Intent to pass in AlarmManager
        return PendingIntent.getBroadcast(mContext, 0, broadcastIntent, FLAG_IMMUTABLE)

    }


}
