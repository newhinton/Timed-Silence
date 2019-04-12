package de.felixnuesse.timedsilence

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 10.04.19 - 18:07
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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDateTime
import android.support.v4.app.NotificationCompat.getExtras
import android.os.Bundle



class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val current = LocalDateTime.now()
        Log.e(Constants.APP_NAME, "Alarmintent: Recieved Alarmintent at: $current")

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME)){
            switchVolumeMode(context)
            Log.e(Constants.APP_NAME, "Alarmintent: Content is to \"check the time\"")


        }

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_DELAY)){

            val extra = intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION_DELAY_EXTRA)
            Log.e(Constants.APP_NAME, "Alarmintent: Content is to \""+extra+"\"")

            if (extra.equals(Constants.BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW)) {
                Log.e(Constants.APP_NAME, "Alarmintent: Content is to \"Restart recurring alarms\"")
                AlarmHandler.createRepeatingTimecheck(context!!)
            }

        }
    }

    fun switchVolumeMode(context: Context?){

        val hour= LocalDateTime.now().hour
        val min= LocalDateTime.now().minute


        if(hour in 0..8 || hour in 22..24){
            val copy = context
            if (copy != null) {
                // copy is guaranteed to be to non-nullable whatever you do
                VolumeHandler.setSilent(copy)
                Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set silent!")
            }
        }

        if(hour in 8..16){
            val copy = context
            if (copy != null) {
                VolumeHandler.setVibrate(copy)
                Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set vibrate!")
            }
        }

        if(hour in 16..22){
            val copy = context
            if (copy != null) {
                VolumeHandler.setLoud(copy)
                Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set loud!")
            }
        }


    }

    fun Any?.notNull(f: ()-> Unit){
        if (this != null){
            f()
        }
    }
}