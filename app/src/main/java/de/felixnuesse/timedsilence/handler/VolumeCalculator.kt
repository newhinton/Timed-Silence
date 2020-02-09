package de.felixnuesse.timedsilence.handler

import android.content.Context
import androidx.core.app.NotificationManagerCompat
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.fragments.TimeFragment
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.LocationAccessMissingNotification
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import java.time.ZoneId.systemDefault
import java.time.Instant.ofEpochMilli
import java.time.ZoneId


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


    private lateinit var cachedTime: LocalDateTime
    var nonNullContext: Context
    var volumeHandler: VolumeHandler
    var dbHandler: DatabaseHandler
    var cached: Boolean = false

    lateinit var calendarHandler:CalendarHandler


    constructor(context: Context) {
        nonNullContext = context
        this.volumeHandler = VolumeHandler()
        dbHandler = DatabaseHandler(nonNullContext)
        calendarHandler= CalendarHandler(nonNullContext)
    }

    constructor(context: Context, cached: Boolean) {
        nonNullContext = context
        this.volumeHandler = VolumeHandler()
        this.cached=cached
        dbHandler = DatabaseHandler(nonNullContext)
        dbHandler.setCaching(cached)
        calendarHandler= CalendarHandler(nonNullContext)
    }


    fun calculateAllAndApply(){
        volumeHandler= VolumeHandler()
        switchBasedOnWifi()
        switchBasedOnTime()
        switchBasedOnCalendar()
        volumeHandler.applyVolume(nonNullContext)
    }

    fun getState(): Int{
        return getStateAt(Date().time)
    }

    fun getStateAt(timeInMilliseconds: Long): Int{

        volumeHandler= VolumeHandler()
        switchBasedOnTime(timeInMilliseconds)
        switchBasedOnCalendar(timeInMilliseconds)

        return volumeHandler.getVolume()

    }

    fun switchBasedOnCalendar(){
        switchBasedOnCalendar(Date().time)
    }

    private fun switchBasedOnCalendar(timeInMilliseconds: Long){
        calendarHandler.enableCaching(cached)

        Log.d(APP_NAME, "VolumeCalculator: Start CalendarCheck")
        for (elem in calendarHandler.readCalendarEvent(timeInMilliseconds)){

            val x = ""//calendarHandler.getDate(elem.get("start_date") ?: "0")
            val y = ""//calendarHandler.getDate(elem.get("end_date") ?: "0")


            //Log.i(APP_NAME, x+ " | " + elem["duration"] + " | " +y+" | "+ elem.get("name_of_event")+ " | recurring:" + elem["recurring"]  + " | "+elem.get("calendar_id"))
            //Log.i(APP_NAME, x+ " | " + timeInMilliseconds + " | " +y+" | "+ elem["duration"] + " | " + elem.get("name_of_event")+ " | recurring:" + elem["recurring"]  + " | "+elem.get("calendar_id"))

            try {
                val currentMilliseconds =  timeInMilliseconds
                val starttime = elem.get("start_date")!!.toLong()

                var endtime: Long= 0
                if(elem.get("end_date")!=null){
                    endtime = elem.get("end_date")!!.toLong()
                }else if (elem.get("duration")!=null){
                    endtime = starttime+elem.get("duration")!!.toLong()
                }
                val volume = calendarHandler.getCalendarVolumeSetting(elem.get("calendar_id")!!.toLong())
                //Log.i(APP_NAME, elem.get("name_of_event")+ " | " + volume  + " ")
                if(volume==-1){
                    continue
                }else{
                    if (currentMilliseconds in (starttime + 1) until endtime-1){
                        Log.i(APP_NAME, elem.get("name_of_event")+" "+elem.get("start_date")+" "+elem.get("end_date")+" "+elem.get("calendar_id")+" "+volume)

                        if (volume == Constants.TIME_SETTING_SILENT) {
                            volumeHandler.setSilent()
                            Log.d(APP_NAME, "Alarmintent: Calendar: (${elem.get("calendar_id")}): Set silent!")
                        }

                        if (volume == Constants.TIME_SETTING_VIBRATE) {
                            volumeHandler.setVibrate()
                            Log.d(Constants.APP_NAME, "Alarmintent: Calendar: (${elem.get("calendar_id")}): Set vibrate!")
                        }

                        if (volume == Constants.TIME_SETTING_LOUD) {
                            volumeHandler.setLoud()
                            Log.d(Constants.APP_NAME, "Alarmintent: Calendar: (${elem.get("calendar_id")}): Set loud!")
                        }
                    }
                }
            }catch (e:Exception ){
                //e.printStackTrace()
                System.err.println("ERROR: "+elem.get("name_of_event")+" "+elem.get("start_date")+" "+elem.get("end_date")+" "+elem.get("description")+" ")
            }

        }
    }

    fun switchBasedOnTime(){
        switchBasedOnTime(Date().time)
    }

    private fun switchBasedOnTime(timeInMilliseconds: Long){
        switchBasedOnTime(timeInMilliseconds, false)
    }

    private fun switchBasedOnTime(timeInMilliseconds: Long, useCachedTime: Boolean){

        Log.d(APP_NAME, "VolumeCalculator: Start TimeCheck")

        val time: LocalDateTime
        if (::cachedTime.isInitialized && useCachedTime) {
            time=cachedTime
        }else{
            time = ofEpochMilli(timeInMilliseconds).atZone(systemDefault()).toLocalDateTime()
            if(useCachedTime){
                cachedTime=time
            }
        }


        Log.d(APP_NAME, "VolumeCalculator: Start TimeCheck: "+time.toString())

        val hour =time.hour
        val min = time.minute

        val dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        Calendar.SATURDAY
        loop@ for (it in dbHandler.getAllSchedules()) {
            val time = hour * 60 * 60 * 1000 + min * 60 * 1000

            //Log.d(Constants.APP_NAME, "Alarmintent: Current Schedule: ${it.name}")
            //Log.d(Constants.APP_NAME, "Alarmintent: Current Weekday: $dayLongName ($dayOfWeek)")

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

            //Log.d(Constants.APP_NAME, "Alarmintent: isAllowedDay: $isAllowedDay")
            if (!isAllowedDay) {
                continue@loop
            }

            var isInInversedTimeInterval = false
            if (it.time_end <= it.time_start) {
                //Log.e(Constants.APP_NAME, "Alarmintent: End is before or equal start")

                if (time >= it.time_start && time < 24 * 60 * 60 * 1000) {
                    //Log.d(Constants.APP_NAME, "Alarmintent: Current time is after start time of interval but before 0:00" )
                    isInInversedTimeInterval = true
                }

                if (time < it.time_end && time >= 0) {
                    //Log.d(Constants.APP_NAME, "Alarmintent: Current time is before end time of interval but after 0:00")
                    isInInversedTimeInterval = true
                }
            }

            if (time in it.time_start..it.time_end || isInInversedTimeInterval) {

                if (it.time_setting == Constants.TIME_SETTING_SILENT) {
                    volumeHandler.setSilent()
                    Log.d(APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set silent!")
                }

                if (it.time_setting == Constants.TIME_SETTING_VIBRATE) {
                    volumeHandler.setVibrate()
                    Log.d(APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set vibrate!")
                }

                if (it.time_setting == Constants.TIME_SETTING_LOUD) {
                    volumeHandler.setLoud()
                    Log.d(APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set loud!")
                }

            }
        }
    }

    fun switchBasedOnWifi(){

        Log.d(APP_NAME, "VolumeCalculator: Start WifiCheck")
        //Log.d(Constants.APP_NAME, "WifiFragment: DatabaseResuluts: Size: " + dbHandler.getAllWifiEntries().size)
        if (dbHandler.getAllWifiEntries().size > 0) {
            val isLocationEnabled = LocationHandler.checkIfLocationServiceIsEnabled(nonNullContext)
            if (!isLocationEnabled) {
                with(NotificationManagerCompat.from(nonNullContext)) {
                    //Log.d(Constants.APP_NAME, "Alarmintent: Locationstate: Disabled!")
                    notify(
                        LocationAccessMissingNotification.NOTIFICATION_ID,
                        LocationAccessMissingNotification.buildNotification(nonNullContext)
                    )
                }
            } else {
                val currentSSID = WifiHandler.getCurrentSsid(nonNullContext)
                dbHandler.getAllWifiEntries().forEach {

                    val ssidit = "\"" + it.ssid + "\""
                    //Log.d(Constants.APP_NAME, "Alarmintent: WifiCheck: check it: " + ssidit + ": " + it.type)
                    if (currentSSID.equals(ssidit) && it.type == Constants.WIFI_TYPE_CONNECTED) {
                        if (it.volume == Constants.TIME_SETTING_LOUD) {
                            volumeHandler.setLoud()
                            //Log.d(Constants.APP_NAME,"Alarmintent: WifiCheck: Set lout, because Connected to $currentSSID")
                        }
                        if (it.volume == Constants.TIME_SETTING_SILENT) {
                            volumeHandler.setSilent()
                            //Log.d(Constants.APP_NAME,"Alarmintent: WifiCheck: Set silent, because Connected to $currentSSID")
                        }
                        if (it.volume == Constants.TIME_SETTING_VIBRATE) {
                            volumeHandler.setVibrate()
                            //Log.d(Constants.APP_NAME, "Alarmintent: WifiCheck: Set vibrate, because Connected to $currentSSID")
                        }
                        return
                    }
                }
            }
        }
    }

}