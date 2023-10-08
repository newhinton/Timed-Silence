package de.felixnuesse.timedsilence.handler.trigger

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants.Companion.PREF_RUN_ALARMTRIGGER_WHEN_IDLE
import de.felixnuesse.timedsilence.Utils
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.receiver.AlarmBroadcastReceiver


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


class TargetedAlarmHandler(override var mContext: Context) : TriggerInterface {

    override fun createTimecheck() {
        createAlarmIntime()
    }

    override fun removeTimecheck() {
        val alarms = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarms.cancel(createBroadcast())
        createBroadcast()?.cancel()

        if(!checkIfNextAlarmExists()){
            Log.d(Constants.APP_NAME, "AlarmHandler: Recurring alarm canceled")
            return
        }
        Log.e(Constants.APP_NAME, "AlarmHandler: Error canceling recurring alarm!")
    }

    override fun createBroadcast(): PendingIntent {
        return createBroadcast(FLAG_IMMUTABLE)
    }

    override fun createBroadcast(flag: Int): PendingIntent {

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
            Constants.BROADCAST_INTENT_ACTION,
            Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME
        )

        // The Pending Intent to pass in AlarmManager
        return PendingIntent.getBroadcast(mContext,0, broadcastIntent, flag or FLAG_IMMUTABLE)
    }


    private fun createAlarmIntime(){
        System.err.println("start create")
        val now = System.currentTimeMillis()
        var calculatedChecktime = 0L
        val list = VolumeHandler(mContext).getChangeList(mContext)
        for (it in list) {
            //Log.e(Constants.APP_NAME, "Calculated time $it")
            //Log.e(Constants.APP_NAME, "Calculated time ${Utils.getDate(calculatedChecktime)}")
            if(it > now && calculatedChecktime == 0L){
                calculatedChecktime = it
            }
        }
        Log.e(Constants.APP_NAME, "Calculated time $calculatedChecktime")
        Log.e(Constants.APP_NAME, "Calculated time ${Utils.getDate(calculatedChecktime)}")

        val am = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi: PendingIntent = createBroadcast()
        am.cancel(pi)


        val sharedPreferences: SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(mContext)
        val allowWhileIdle = sharedPreferences.getBoolean(
            PREF_RUN_ALARMTRIGGER_WHEN_IDLE,
            false
        )

        //todo: fix permission requesting
        if (allowWhileIdle) {
            am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calculatedChecktime,
                pi
            )
        } else {
            am.setExact(
                AlarmManager.RTC_WAKEUP,
                calculatedChecktime,
                pi
            )
        }
    }

}
