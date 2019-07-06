package de.felixnuesse.timedsilence.receiver

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
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.Constants.Companion.WIFI_TYPE_CONNECTED
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.handler.LocationHandler
import de.felixnuesse.timedsilence.handler.VolumeHandler
import de.felixnuesse.timedsilence.handler.WifiHandler
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.LocationAccessMissingNotification
import java.time.LocalDateTime
import java.util.*




class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {

        val current =System.currentTimeMillis()
        val date = Date(current)
        val dateFormat = android.text.format.DateFormat.getDateFormat(context)
        val currentformatted = dateFormat.format(date)

        Log.e(Constants.APP_NAME, "Alarmintent: Recieved Alarmintent at: $currentformatted")

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME)) {
            Log.d(Constants.APP_NAME, "Alarmintent: Content is to \"check the time\"")

            val sharedPref = context?.getSharedPreferences("test", Context.MODE_PRIVATE)
            with(sharedPref!!.edit()) {
                putLong("last_ExecTime", current)
                apply()
            }


            switchVolumeMode(context)

        }

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_DELAY)) {

            val extra = intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION_DELAY_EXTRA)
            Log.d(Constants.APP_NAME, "Alarmintent: Content is to \"" + extra + "\"")

            if (extra.equals(Constants.BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW)) {
                Log.d(Constants.APP_NAME, "Alarmintent: Content is to \"Restart recurring alarms\"")
                AlarmHandler.createRepeatingTimecheck(context!!)
            }

        }
    }

    fun switchVolumeMode(context: Context?) {


        val nonNullContext = context
        // copy is guaranteed to be to non-nullable whatever you do
        if (nonNullContext == null) {
            Log.e(Constants.APP_NAME, "Alarmintent: Error! Context invalid! Stopping!")
            return
        }


        val db = DatabaseHandler(nonNullContext)
        Log.d(Constants.APP_NAME, "WifiFragment: DatabaseResuluts: Size: " + db.getAllWifiEntries().size)
        if (db.getAllWifiEntries().size > 0) {
            val isLocationEnabled = LocationHandler.checkIfLocationServiceIsEnabled(nonNullContext)
            if (!isLocationEnabled) {
                with(NotificationManagerCompat.from(nonNullContext)) {
                    Log.d(Constants.APP_NAME, "Alarmintent: Locationstate: Disabled!")
                    notify(
                        LocationAccessMissingNotification.NOTIFICATION_ID,
                        LocationAccessMissingNotification.buildNotification(nonNullContext)
                    )
                }
            } else {
                val currentSSID = WifiHandler.getCurrentSsid(nonNullContext)
                db.getAllWifiEntries().forEach {

                    val ssidit = "\"" + it.ssid + "\""
                    Log.d(Constants.APP_NAME, "Alarmintent: WifiCheck: check it: " + ssidit + ": " + it.type)
                    if (currentSSID.equals(ssidit) && it.type == WIFI_TYPE_CONNECTED) {
                        if (it.volume == TIME_SETTING_LOUD) {
                            VolumeHandler.setLoud(nonNullContext)
                            Log.d(
                                Constants.APP_NAME,
                                "Alarmintent: WifiCheck: Set lout, because Connected to $currentSSID"
                            )
                        }
                        if (it.volume == TIME_SETTING_SILENT) {
                            VolumeHandler.setSilent(nonNullContext)
                            Log.d(
                                Constants.APP_NAME,
                                "Alarmintent: WifiCheck: Set silent, because Connected to $currentSSID"
                            )
                        }
                        if (it.volume == TIME_SETTING_VIBRATE) {
                            VolumeHandler.setVibrate(nonNullContext)
                            Log.d(
                                Constants.APP_NAME,
                                "Alarmintent: WifiCheck: Set vibrate, because Connected to $currentSSID"
                            )
                        }
                        return
                    }
                }
            }
        }
        val hour = LocalDateTime.now().hour
        val min = LocalDateTime.now().minute

        val dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        loop@ for (it in DatabaseHandler(context).getAllSchedules()) {
            val time = hour * 60 * 60 * 1000 + min * 60 * 1000

            Log.d(Constants.APP_NAME, "Alarmintent: Current Schedule: ${it.name}")
            Log.d(Constants.APP_NAME, "Alarmintent: Current Weekday: $dayLongName")

            var isAllowedDay = false
            when (dayOfWeek){
                2 -> if (it.mon) { isAllowedDay = true }
                3 -> if (it.tue) { isAllowedDay = true }
                4 -> if (it.wed) { isAllowedDay = true }
                5 -> if (it.thu) { isAllowedDay = true }
                6 -> if (it.fri) { isAllowedDay = true }
                0 -> if (it.sat) { isAllowedDay = true }
                1 -> if (it.sun) { isAllowedDay = true }
            }

            if (!isAllowedDay) {
                continue@loop
            }

            var isInInversedTimeInterval = false
            if (it.time_end <= it.time_start) {
                Log.e(Constants.APP_NAME, "Alarmintent: End is before or equal start")

                if (time >= it.time_start && time < 24 * 60 * 60 * 1000) {
                    Log.d(
                        Constants.APP_NAME,
                        "Alarmintent: Current time is after start time of interval but before 0:00"
                    )
                    isInInversedTimeInterval = true
                }

                if (time < it.time_end && time >= 0) {
                    Log.d(Constants.APP_NAME, "Alarmintent: Current time is before end time of interval but after 0:00")
                    isInInversedTimeInterval = true
                }
            }

            if (time in it.time_start..it.time_end || isInInversedTimeInterval) {

                if (it.time_setting == TIME_SETTING_SILENT) {
                    VolumeHandler.setSilent(nonNullContext)
                    Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set silent!")
                }

                if (it.time_setting == TIME_SETTING_VIBRATE) {
                    VolumeHandler.setVibrate(nonNullContext)
                    Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set vibrate!")
                }

                if (it.time_setting == TIME_SETTING_LOUD) {
                    VolumeHandler.setLoud(nonNullContext)
                    Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set loud!")
                }

            }
        }
    }


    fun Any?.notNull(f: () -> Unit) {
        if (this != null) {
            f()
        }
    }
}