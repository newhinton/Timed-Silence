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
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.ui.notifications.LocationAccessMissingNotification
import de.felixnuesse.timedsilence.util.DateUtil
import java.time.LocalDateTime
import java.util.*
import java.time.ZoneId.systemDefault
import java.time.Instant.ofEpochMilli
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.TimeUnit
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
        LogHandler.writeLog(nonNullContext,"VolumeCalculator", "instantiate","VolumeCalculator was now instantiated")
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
        return getChangeList(true)
    }
    fun getChangeList(addNow: Boolean): ArrayList<VolumeState> {
        var start = System.currentTimeMillis()
        Log.e(TAG, "Starttime: "+start)

        val midnight: LocalTime = LocalTime.MIDNIGHT
        val today: LocalDate = LocalDate.now(systemDefault())
        val now: LocalDateTime = LocalDateTime.now(systemDefault())
        val nowAsOffset = now.minute+now.hour*60
        var todayMidnight = LocalDateTime.of(today, midnight)

        var stateList = arrayListOf<VolumeState>()


        val timeInitial = todayMidnight.plusMinutes(0).atZone(systemDefault()).toInstant().toEpochMilli()
        stateList.add(getStateAt(nonNullContext, timeInitial, 0))
        var lastState = stateList[0]


        var possibleChangeList: ArrayList<Long> = ArrayList()

        if(addNow){
            // we dont talk about this monstrosity
            possibleChangeList.add(
                now.atZone(systemDefault())
                    .toInstant()
                    .toEpochMilli()
                        -
                        todayMidnight.atZone(systemDefault())
                            .toInstant()
                            .toEpochMilli()
            )
        }

        calendarHandler.getFilteredEventsForDay(System.currentTimeMillis()).forEach {
            Log.e(TAG, ""+it.mStart)
            possibleChangeList.add(it.mStart-todayMidnight.atZone(systemDefault()).toInstant().toEpochMilli())
            possibleChangeList.add(it.mEnd-todayMidnight.atZone(systemDefault()).toInstant().toEpochMilli())
        }
        dbHandler.getSchedulesForWeekday(now.dayOfWeek).forEach {
            possibleChangeList.add(it.time_start)
            possibleChangeList.add(it.time_end)
        }

        possibleChangeList.sort()
        for(possibleChange in possibleChangeList){

            var elem = TimeUnit.MILLISECONDS.toMinutes(possibleChange)
            val time = todayMidnight.plusMinutes(elem).atZone(systemDefault()).toInstant().toEpochMilli()

            val currentState = getStateAt(nonNullContext, time, elem.toInt())
            if(currentState.state != lastState.state || elem.toInt() == nowAsOffset) {
                Log.e(TAG, "($elem) Switch from ${lastState.stateString()} to ${currentState.stateString()} because of ${currentState.getReason()}")
                if(elem.toInt() == nowAsOffset){
                    Log.e(TAG, "($elem) Added because it is the current time")
                }

                lastState.endTime = currentState.startTime-1
                lastState = currentState
                stateList.add(currentState)
            }
        }

        var lastElement = stateList.last()
        lastElement.endTime = 1440


        var end = System.currentTimeMillis()
        Log.e(TAG, "Endtime: $end")
        Log.e(TAG, "Diff: ${end-start}ms")
        return stateList
    }

    fun calculateAllAndApply(){
        LogHandler.writeLog(nonNullContext,"VolumeCalculator", "calculateAllAndApply called","Start Calculating")
        volumeHandler = VolumeHandler(nonNullContext)
        switchBasedOnWifi()
        switchBasedOnTime()
        switchBasedOnCalendar()
        volumeHandler.applyVolume(nonNullContext)
    }

    fun getStateAt(context: Context, timeInMilliseconds: Long): VolumeState{
        return getStateAt(context, timeInMilliseconds, 0)
    }
    fun getStateAt(context: Context, timeInMilliseconds: Long, timeAsOffset: Int): VolumeState {

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

        for (elem in calendarHandler.getFilteredEventsForDay(timeInMilliseconds)){

            try {

                val starttime = elem.mStart

                var endtime = elem.mEnd

                for (keyword in dbHandler.getKeywords()){
                    val desc = elem.mDescription.toLowerCase(Locale.getDefault())
                    val name = elem.mTitle.toLowerCase(Locale.getDefault())
                    val key = keyword.keyword.toLowerCase(Locale.getDefault())

                    //Log.e(TAG, "Check Keyword: $key")

                    if(desc.contains(key) || name.contains(key)){
                        //Log.e(APP_NAME, "Keyword: $key is in current element $name")
                        if (timeInMilliseconds in (starttime) until endtime) {
                            //Log.e(APP_NAME, "Keyword: $key is in time")
                            setGenericVolumeWithReason(
                                keyword.volume,
                                keyword.keyword,
                                REASON_KEYWORD
                            )

                        }
                    }
                }

                var calendarName = calendarHandler.getCalendarName(elem.mCalendarID.toLong())
                var eventName = elem.mTitle
                var volume = calendarHandler.getCalendarVolumeSetting(calendarName)
                //Log.i(TAG, elem.mTitle+ " | " + volume  + " ")

                if(volume==-1){
                    continue
                }else{
                    //Log.i(TAG, "${DateUtil.getDate(timeInMilliseconds)}: ${elem.mTitle} ${starttime/10000} ${(timeInMilliseconds-starttime)/10000} ${(endtime-starttime)/10000} ${elem.mCalendarID} $volume")
                    if (timeInMilliseconds in (starttime) until endtime) {
                        setGenericVolumeWithReason(
                            volume,
                            "$calendarName ($eventName)",
                            REASON_CALENDAR
                        )
                    }
                }
            }catch (e:Exception ){
                e.printStackTrace()
                System.err.println("ERROR: ${elem.mTitle} ${elem.mStart} ${elem.mEnd} ${elem.mDescription}")
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