package de.felixnuesse.timedsilence.handler

import android.content.Context
import android.support.v4.app.NotificationManagerCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.fragments.TimeFragment
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.LocationAccessMissingNotification
import java.time.LocalDateTime
import java.util.*

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 13.11.19 - 15:25
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


class VolumeCalculator {


    var nonNullContext: Context
    var volumeHandler: VolumeHandler


    constructor(context: Context, volumeHandler: VolumeHandler) {
        nonNullContext = context
        this.volumeHandler = volumeHandler
    }


    fun calcAndApply(){
        switchBasedOnWifi()
        switchBasedOnTime()
        switchBasedOnCalendar()
        volumeHandler.applyVolume(nonNullContext)
    }


    fun switchBasedOnCalendar(){
        switchBasedOnCalendar(Date().getTime())
    }

    fun switchBasedOnCalendar(timeInMilliseconds: Long){
        val ch = CalendarHandler(nonNullContext)

        Log.e(APP_NAME, "switch based on cal")
        for (elem in ch.readCalendarEvent()){
            //println(elem.get("name_of_event")+" "+elem.get("start_date")+" "+elem.get("end_date")+" "+elem.get("description")+" ")

            try {
                val currentMilliseconds =  timeInMilliseconds
                val starttime = elem.get("start_date")!!.toLong()

                var endtime: Long= 0
                if(elem.get("end_date")!=null){
                    endtime = elem.get("end_date")!!.toLong()
                }else if (elem.get("duration")!=null){
                    endtime = starttime+elem.get("duration")!!.toLong()
                }
                val volume = ch.getCalendarVolumeSetting(elem.get("calendar_id")!!.toLong())
                //println(elem.get("name_of_event")+" "+volume)

                if(volume==-1){
                    continue
                }else{
                    if (currentMilliseconds in (starttime + 1)..(endtime - 1)){
                        println(elem.get("name_of_event")+" "+elem.get("start_date")+" "+elem.get("end_date")+" "+elem.get("calendar_id")+" "+volume)

                        if (volume == Constants.TIME_SETTING_SILENT) {
                            volumeHandler.setSilent()
                            Log.e(Constants.APP_NAME, "Alarmintent: Calendar: (${elem.get("calendar_id")}): Set silent!")
                        }

                        if (volume == Constants.TIME_SETTING_VIBRATE) {
                            volumeHandler.setVibrate()
                            Log.e(Constants.APP_NAME, "Alarmintent: Calendar: (${elem.get("calendar_id")}): Set vibrate!")
                        }

                        if (volume == Constants.TIME_SETTING_LOUD) {
                            volumeHandler.setLoud()
                            Log.e(Constants.APP_NAME, "Alarmintent: Calendar: (${elem.get("calendar_id")}): Set loud!")
                        }
                    }
                }
            }catch (e:Exception ){
                //e.printStackTrace()
                println("ERROR: "+elem.get("name_of_event")+" "+elem.get("start_date")+" "+elem.get("end_date")+" "+elem.get("description")+" ")
            }

        }
    }

    fun switchBasedOnTime(){
        val hour = LocalDateTime.now().hour
        val min = LocalDateTime.now().minute

        val dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        Calendar.SATURDAY
        loop@ for (it in DatabaseHandler(nonNullContext).getAllSchedules()) {
            val time = hour * 60 * 60 * 1000 + min * 60 * 1000

            Log.d(Constants.APP_NAME, "Alarmintent: Current Schedule: ${it.name}")
            Log.d(Constants.APP_NAME, "Alarmintent: Current Weekday: $dayLongName ($dayOfWeek)")

            var isAllowedDay = false
            when (dayOfWeek){
                2 -> if (it.mon) { isAllowedDay = true }
                3 -> if (it.tue) { isAllowedDay = true }
                4 -> if (it.wed) { isAllowedDay = true }
                5 -> if (it.thu) { isAllowedDay = true }
                6 -> if (it.fri) { isAllowedDay = true }
                7 -> if (it.sat) { isAllowedDay = true }
                1 -> if (it.sun) { isAllowedDay = true }
            }

            Log.d(Constants.APP_NAME, "Alarmintent: isAllowedDay: $isAllowedDay")
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

                if (it.time_setting == Constants.TIME_SETTING_SILENT) {
                    volumeHandler.setSilent()
                    Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set silent!")
                }

                if (it.time_setting == Constants.TIME_SETTING_VIBRATE) {
                    volumeHandler.setVibrate()
                    Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set vibrate!")
                }

                if (it.time_setting == Constants.TIME_SETTING_LOUD) {
                    volumeHandler.setLoud()
                    Log.e(Constants.APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set loud!")
                }

            }
        }
    }

    fun switchBasedOnWifi(){
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
                    if (currentSSID.equals(ssidit) && it.type == Constants.WIFI_TYPE_CONNECTED) {
                        if (it.volume == Constants.TIME_SETTING_LOUD) {
                            volumeHandler.setLoud()
                            Log.d(
                                Constants.APP_NAME,
                                "Alarmintent: WifiCheck: Set lout, because Connected to $currentSSID"
                            )
                        }
                        if (it.volume == Constants.TIME_SETTING_SILENT) {
                            volumeHandler.setSilent()
                            Log.d(
                                Constants.APP_NAME,
                                "Alarmintent: WifiCheck: Set silent, because Connected to $currentSSID"
                            )
                        }
                        if (it.volume == Constants.TIME_SETTING_VIBRATE) {
                            volumeHandler.setVibrate()
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
    }

}