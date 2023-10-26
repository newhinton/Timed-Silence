package de.felixnuesse.timedsilence.handler.volume

import android.content.Context
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.REASON_CALENDAR
import de.felixnuesse.timedsilence.Constants.Companion.REASON_KEYWORD
import de.felixnuesse.timedsilence.Constants.Companion.REASON_TIME
import de.felixnuesse.timedsilence.Constants.Companion.REASON_UNDEFINED
import de.felixnuesse.timedsilence.Constants.Companion.REASON_WIFI
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.calculator.CalendarHandler
import de.felixnuesse.timedsilence.handler.calculator.LocationHandler
import de.felixnuesse.timedsilence.handler.calculator.WifiHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.notifications.LocationAccessMissingNotification
import java.time.LocalDateTime
import java.util.*
import java.time.ZoneId.systemDefault
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.LocalTime
import kotlin.collections.ArrayList


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

    companion object {
        private const val TAG = "VolumeCalculator"
    }

    private lateinit var cachedTime: LocalDateTime
    private var nonNullContext: Context
    private var volumeHandler: VolumeHandler
    private var dbHandler: DatabaseHandler
    private var mCached: Boolean = false

    private var calendarHandler: CalendarHandler

    private var changeReason = REASON_UNDEFINED
    private var changeReasonString = ""

    private var ignoreMusicPlaying = false

    constructor(context: Context) {
        nonNullContext = context
        volumeHandler = VolumeHandler(context)
        dbHandler = DatabaseHandler(nonNullContext)
        calendarHandler= CalendarHandler(nonNullContext)
    }

    constructor(context: Context, cached: Boolean) {
        nonNullContext = context
        volumeHandler = VolumeHandler(nonNullContext)
        mCached=cached
        dbHandler = DatabaseHandler(nonNullContext)
        dbHandler.setCaching(cached)
        calendarHandler= CalendarHandler(nonNullContext)
    }

    fun ignoreMusicPlaying(ignore: Boolean) {
        ignoreMusicPlaying = ignore
    }

    fun getChangeList(): ArrayList<VolumeState> {

        val midnight: LocalTime = LocalTime.MIDNIGHT
        val today: LocalDate = LocalDate.now(systemDefault())
        var todayMidnight = LocalDateTime.of(today, midnight)

        var stateList = arrayListOf<VolumeState>()


        val timeInitial = todayMidnight.plusMinutes(0).atZone(systemDefault()).toInstant().toEpochMilli()
        stateList.add(getStateAt(nonNullContext, timeInitial, 0))
        var lastState = stateList[0]

        for(elem in 0L..1440L){

            val time = todayMidnight.plusMinutes(elem).atZone(systemDefault()).toInstant().toEpochMilli()

            val currentState = getStateAt(nonNullContext, time, elem.toInt())
            if(currentState.state != lastState.state) {
                Log.e("TAG", "($elem) Switch from ${lastState.state} to ${currentState.state} because of ${currentState.getReason()}")

                lastState.endTime = currentState.startTime-1
                lastState = currentState
                stateList.add(currentState)
            }
        }


        var lastElement = stateList.last()
        lastElement.endTime = 1440


        return stateList
    }

    fun calculateAllAndApply(){
        LogHandler.writeAppLog(nonNullContext,"VolCacl: calculateAllAndApply called.")
        volumeHandler = VolumeHandler(nonNullContext)
        switchBasedOnWifi()
        switchBasedOnTime()
        switchBasedOnCalendar()
        volumeHandler.applyVolume(nonNullContext)
    }

    fun getStateAt(context: Context, timeInMilliseconds: Long): VolumeState{
        return getStateAt(context, timeInMilliseconds, 0)
    }
    fun getStateAt(context: Context, timeInMilliseconds: Long, timeAsOffset: Int): VolumeState{

        volumeHandler = VolumeHandler(context)
        switchBasedOnTime(timeInMilliseconds)
        switchBasedOnCalendar(timeInMilliseconds)
        val state = VolumeState(timeAsOffset, volumeHandler.getVolume(), changeReason, changeReasonString)
        changeReason = REASON_UNDEFINED
        changeReasonString = ""
        return state

    }

    fun switchBasedOnCalendar(){
        switchBasedOnCalendar(Date().time)
    }

    private fun switchBasedOnCalendar(timeInMilliseconds: Long){
        //Log.d(APP_NAME, "VolumeCalculator: Start CalendarCheck")

        //todo: switch to checking of calendar entries and THEN device calendar entries to increase performance
        // for now we only check it once and abort before getting all calendars.
        if(calendarHandler.getVolumeCalendars().size==0){
            return
        }

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

                for (keyword in dbHandler.getKeywords()){
                    val desc = elem.getOrDefault("description", "").toLowerCase(Locale.getDefault())
                    val name = elem.getOrDefault("name_of_event", "").toLowerCase(Locale.getDefault())
                    val key = keyword.keyword.toLowerCase(Locale.getDefault())

                    //Log.e(APP_NAME, "Check Keyword: $key")

                    if(desc.contains(key) || name.contains(key)){
                        //Log.e(APP_NAME, "Keyword: $key is in current element $name")
                        if (currentMilliseconds in (starttime + 1) until endtime-1){
                            //Log.e(APP_NAME, "Keyword: $key is in time")
                            setGenericVolumeWithReason(keyword.volume, keyword.keyword, REASON_KEYWORD)

                        }
                    }
                }

                var calendar_id = elem.getOrDefault("calendar_id","-1").toLong()
                var calendar_name = calendarHandler.getCalendarName(calendar_id)
                var eventName =elem.get("name_of_event")
                var volume = calendarHandler.getCalendarVolumeSetting(calendar_name)
                //Log.i(APP_NAME, elem.get("name_of_event")+ " | " + volume  + " ")

                if(volume==-1){
                    continue
                }else{
                    if (currentMilliseconds in (starttime + 1) until endtime-1){
                        //Log.i(APP_NAME, elem.get("name_of_event")+" "+elem.get("start_date")+" "+elem.get("end_date")+" "+elem.get("calendar_id")+" "+volume)
                        setGenericVolumeWithReason(volume, "$calendar_name ($eventName)", REASON_CALENDAR)
                    }
                }
            }catch (e:Exception ){
                e.printStackTrace()
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

        //Log.d(APP_NAME, "VolumeCalculator: Start TimeCheck")

        val time: LocalDateTime
        if (::cachedTime.isInitialized && useCachedTime) {
            time=cachedTime
        }else{
            time = ofEpochMilli(timeInMilliseconds).atZone(systemDefault()).toLocalDateTime()
            if(useCachedTime){
                cachedTime=time
            }
        }

        //Log.d(APP_NAME, "VolumeCalculator: Start TimeCheck: "+time.toString())

        val hour =time.hour
        val min = time.minute

        //val dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault())
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)

        Calendar.SATURDAY
        loop@ for (it in dbHandler.getAllSchedules()) {
            val time = hour * 60 * 60 * 1000 + min * 60 * 1000

            //Log.d(TAG, "Alarmintent: Current Schedule: ${it.name}")
            //Log.d(TAG, "Alarmintent: Current Weekday: $dayLongName ($dayOfWeek)")

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

            //Log.d(TAG, "Alarmintent: isAllowedDay: $isAllowedDay")
            if (!isAllowedDay) {
                continue@loop
            }

            var isInInversedTimeInterval = false
            if (it.time_end <= it.time_start) {
                //Log.e(TAG, "Alarmintent: End is before or equal start")

                if (time >= it.time_start && time < 24 * 60 * 60 * 1000) {
                    //Log.d(TAG, "Alarmintent: Current time is after start time of interval but before 0:00" )
                    isInInversedTimeInterval = true
                }

                if (time < it.time_end && time >= 0) {
                    //Log.d(TAG, "Alarmintent: Current time is before end time of interval but after 0:00")
                    isInInversedTimeInterval = true
                }
            }

            if (time in it.time_start..it.time_end || isInInversedTimeInterval) {
                setGenericVolumeWithReason(it.time_setting,it.name, REASON_TIME)
            }
        }
    }

    private fun setGenericVolumeWithReason(volume: Int, reasonString: String, reason: Int){
        changeReason = reason
        changeReasonString = reasonString
        setGenericVolume(volume)
    }

    private fun setGenericVolume(volume: Int){
        volumeHandler.overrideMusicToZero = ignoreMusicPlaying;
        when (volume) {
            TIME_SETTING_LOUD -> {
                volumeHandler.setLoud()
                //Log.d(APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set loud!")
            }
            TIME_SETTING_VIBRATE -> {
                volumeHandler.setVibrate()
                //Log.d(APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set vibrate!")
            }
            TIME_SETTING_SILENT -> {
                volumeHandler.setSilent()
                //Log.d(APP_NAME, "Alarmintent: Timecheck ($hour:$min): Set silent!")
            }
        }
    }

    fun switchBasedOnWifi(){

        //Log.d(APP_NAME, "VolumeCalculator: Start WifiCheck")
        //Log.d(TAG, "WifiFragment: DatabaseResuluts: Size: " + dbHandler.getAllWifiEntries().size)
        if (dbHandler.getAllWifiEntries().size > 0) {
            val isLocationEnabled =
                LocationHandler.checkIfLocationServiceIsEnabled(
                    nonNullContext
                )
            if (!isLocationEnabled) {
                with(NotificationManagerCompat.from(nonNullContext)) {
                    //Log.d(TAG, "Alarmintent: Locationstate: Disabled!")
                    notify(
                        LocationAccessMissingNotification.NOTIFICATION_ID,
                        LocationAccessMissingNotification.buildNotification(nonNullContext)
                    )
                }
            } else {
                val currentSSID =
                    WifiHandler.getCurrentSsid(
                        nonNullContext
                    )
                dbHandler.getAllWifiEntries().forEach {

                    val ssidit = "\"" + it.ssid + "\""
                    //Log.d(TAG, "Alarmintent: WifiCheck: check it: " + ssidit + ": " + it.type)
                    if (currentSSID.equals(ssidit) && it.type == Constants.WIFI_TYPE_CONNECTED) {
                        setGenericVolumeWithReason(it.volume, it.ssid, REASON_WIFI)
                        return
                    }
                }
            }
        }
    }
}