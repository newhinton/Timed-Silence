package de.felixnuesse.timedsilence.fragments.graph

import android.content.Context
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.handler.VolumeCalculator
import kotlinx.android.synthetic.main.graph_fragment.*
import java.text.SimpleDateFormat
import java.time.*
import java.util.*
import kotlin.contracts.contract

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
class GraphFragmentThread(context: Context, ll: LinearLayout): Thread() {

    val context:Context =context
    val ll: LinearLayout= ll


    public override fun run() {
        println("${Thread.currentThread()} has run.")

    }

    // Implementing the Runnable interface to implement threads.
    class SimpleRunnable: Runnable {
        val context:Context
        val ll: LinearLayout

        constructor(context: Context, ll: LinearLayout){
            this.context=context
            this.ll=ll
        }

        public override fun run() {
            println("${Thread.currentThread()} has run.")
            doIt(context,ll)
        }

        fun doIt(context:Context, barList: LinearLayout){
            var volCalc = VolumeCalculator(context!!, true)
            val s = 1000
            val m = 60*s

            for(elem in 1..144){

                val time =  Instant.ofEpochMilli((elem*m).toLong()).atZone(ZoneId.systemDefault()).toLocalDateTime()
                Log.e("app", "run $elem");
                val hour =time.hour
                val min = time.minute

                val text: TextView = TextView(context)
                text.text = "t- ${time.hour}:${time.minute} "+volCalc.getStateAt((elem*m).toLong())
                barList.addView(text)
            }

        }

    }

    fun doIt(context:Context, barList: LinearLayout){

        barList.removeAllViews()

        var volCalc = VolumeCalculator(context!!, true)

        val midnight: LocalTime = LocalTime.MIDNIGHT
        val today: LocalDate = LocalDate.now(ZoneId.systemDefault())
        var todayMidnight = LocalDateTime.of(today, midnight)

        var lastState = Constants.TIME_SETTING_UNSET
        val lastElem = 1439 //start by 0:00 end by 23:59
        for(elem in 0..lastElem){


            val hoursFromInt = Math.floorDiv(elem, 60)
            val minutesFromInt = elem - (60*hoursFromInt)

            var localMidnight = todayMidnight.plusHours(hoursFromInt.toLong())
            localMidnight = localMidnight.plusMinutes(minutesFromInt.toLong())

            val text = TextView(context)

            val state = volCalc.getStateAt(localMidnight.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())

            //Log.e("app", "run $elem : $state")

            if(lastState!=state || elem == lastElem){// || true){ //
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

                text.text = "$hour:$minute | $localMidnight | $volume" ///+ " |\n " +todayMidnights.toString()
                barList.addView(text)
                lastState=state
            }

        }

    }


}