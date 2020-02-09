package de.felixnuesse.timedsilence.fragments.graph

import android.content.Context
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeCalculator
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.collections.ArrayList

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 14.11.19 - 10:46
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
class GraphFragmentThread(context: Context): Thread() {

    val context:Context =context

    fun doIt(context:Context): List<GraphBarVolumeSwitchElement>{

        var list = ArrayList<GraphBarVolumeSwitchElement>()

        //barList.removeAllViews()

        var volCalc = VolumeCalculator(context!!, true)

        val midnight: LocalTime = LocalTime.MIDNIGHT
        val today: LocalDate = LocalDate.now(ZoneId.systemDefault())
        var todayMidnight = LocalDateTime.of(today, midnight)

        var lastState = Constants.TIME_SETTING_UNSET
        val lastElem = 1440 //start by 0:00 end by 23:59

        var lastGraphElement= "-1"


        val rightNow = Calendar.getInstance()
        var currentHour = rightNow.get(Calendar.HOUR_OF_DAY)*60
        currentHour += rightNow.get(Calendar.MINUTE)



        for(elem in 0..lastElem){


            val hoursFromInt = Math.floorDiv(elem, 60)
            val minutesFromInt = elem - (60*hoursFromInt)

            var localMidnight = todayMidnight.plusHours(hoursFromInt.toLong())
            localMidnight = localMidnight.plusMinutes(minutesFromInt.toLong())

            //val text = TextView(context)

            val state = volCalc.getStateAt(localMidnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())

            //Log.e("app", "run $elem : $state")


            var shortFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
            var dt = localMidnight.toLocalTime().format(shortFormat)
            lastGraphElement=dt


            var isnow = false
            if(currentHour==elem){
                isnow=true
            }

            if(lastState!=state || elem == lastElem || isnow){// || true){ //
                var volume = "--"
                when (state) {
                    Constants.TIME_SETTING_SILENT -> volume =  "SILENT"
                    Constants.TIME_SETTING_VIBRATE -> volume = "VIBRATE"
                    Constants.TIME_SETTING_LOUD -> volume =    "LOUD"
                    else -> {
                        // applySilent(context)
                    }
                }

                var hour = hoursFromInt.toString()//time.hour.toString()
                var minute = minutesFromInt.toString()//time.minute.toString()

                if(hour.length!=2){
                    hour = "0$hour"
                }
                if(minute.length!=2){
                    minute = "0$minute"
                }

                //text.text = "$hour:$minute | $localMidnight | $volume" ///+ " |\n " +todayMidnights.toString()
                //barList.addView(text)

                Log.e("app", "run ${elem}: ${state}")

                //var shortFormat = DateTimeFormatter.ISO_LOCAL_TIME
                //var text= DateFormat.format("yyyy-MM-dd hh:mm:ss a", dt).toString()
                //DateUtils.formatDateTime(context, localMidnight.toEpochSecond(ZoneOffset.UTC)!!, 0)
                list.add(GraphBarVolumeSwitchElement(elem, lastState,dt))
                lastState=state
            }

        }

        if(list.size==1){
            list.add(GraphBarVolumeSwitchElement(lastElem, TIME_SETTING_UNSET,lastGraphElement))
        }

        return list
    }


}