package de.felixnuesse.timedsilence.services

import android.app.*
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.R
import android.content.Context
import android.widget.Toast
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import de.felixnuesse.timedsilence.ui.PauseNotification
import java.text.SimpleDateFormat
import java.util.*


/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 21.04.19 - 14:22
 * <p>
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 * <p>
 * <p>
 * This program is released under the GPLv3 license
 * <p>
 * <p>
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p>
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 */


class PauseTimerService : Service() {




    companion object {

        var mCurentLengthIndex : Int = 0
        var mIsRunning: Boolean = false

        var mTimer: CountDownTimer? = null
        var mTimerTimeLeft: Long = -1
        var mTimerTimeInitial: Long = -1

        var mListenerList= arrayListOf<TimerInterface>()

        fun registerListener(listener: TimerInterface){
            if(!mListenerList.contains(listener)){
                Log.e(Constants.APP_NAME,"PauseTimerService: mListenerList registered")
                mListenerList.add(listener)
            }
        }

        fun isTimerRunning(): Boolean{
            return mIsRunning
        }

        /**
         * This starts the autotimer. If it is already running, it cancels it and starts the next autotime in the array.
         */
        fun startAutoTimer(context: Context){
            val i =Intent(context, PauseTimerService::class.java)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_ACTION,Constants.SERVICE_INTENT_DELAY_ACTION)
            Log.e(Constants.APP_NAME,"PauseTileService: service started")
            context.startForegroundService(i)
        }

        /**
         * This starts the autotimer. If it is already running, it cancels it and starts the next autotime in the array.
         */
        fun startTimer(context: Context, timeInMs: Long){
            val i =Intent(context, PauseTimerService::class.java)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_ACTION,Constants.SERVICE_INTENT_DELAY_ACTION)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_AMOUNT, timeInMs)
            Log.e(Constants.APP_NAME,"PauseTileService: service started with custom time")
            context.startForegroundService(i)
        }

        /**
         * This toggles the timer. If it is already running, it cancels it .
         */
        fun toggleTimer(context: Context, timeInMs: Long){
            val i =Intent(context, PauseTimerService::class.java)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_ACTION,Constants.SERVICE_INTENT_DELAY_ACTION_TOGGLE)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_AMOUNT, timeInMs)
            Log.e(Constants.APP_NAME,"PauseTileService: service toggled with custom time")
            context.startForegroundService(i)
        }


        /**
         * This cancels a currently running timer immediately. Also it informs all registered listener that the timer has reached its end and is now finished. Also resets the auto-timer to the first value.
         */
        fun finishTimer(context: Context){

            AlarmHandler.createRepeatingTimecheck(context)
            for (interfaceElement in mListenerList){
                interfaceElement.timerFinished(context)
            }

            mTimer!!.cancel()
            mCurentLengthIndex=0
            mIsRunning=false
        }

        /**
         * This cancels a timer and informs listeners about it beeing finished. Does not restart alarmchecks
         */
        fun cancelTimer(context: Context){

            for (interfaceElement in mListenerList){
                interfaceElement.timerFinished(context)
            }


            mCurentLengthIndex=0
            mTimer!!.cancel()
            mIsRunning=false
        }

        fun getTimestampInProperLength(timeAsLong: Long):String{
            val date = Date(timeAsLong)
            val format: SimpleDateFormat?

            if(timeAsLong>=Constants.HOUR){
                format = SimpleDateFormat("HH:mm:ss")
            }else {
                format = SimpleDateFormat("mm:ss")
            }

            format.timeZone = TimeZone.getTimeZone("UTC")
            return format.format(date)
        }

    }

    override fun onBind(intent: Intent?): IBinder? {

        //super.onBind(intent)
        return null

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //Todo: overhaul this stupid mess

        //super.onBind(intent)
        Log.e(Constants.APP_NAME, "PauseTimerService: Intent is called")

        var toggle=false

        if (intent?.getStringExtra(Constants.SERVICE_INTENT_DELAY_ACTION).equals(Constants.SERVICE_INTENT_DELAY_ACTION_TOGGLE)){
            Log.e(Constants.APP_NAME,"PauseTileService: service toggled")
            if(mIsRunning){
                finishTimer(this)
                resetTimerSystem()
            }else{
                toggle=true
            }
        }

        if (intent?.getStringExtra(Constants.SERVICE_INTENT_DELAY_ACTION).equals(Constants.SERVICE_INTENT_DELAY_ACTION) || toggle){

            var time: Long= -1

            Log.e(Constants.APP_NAME, "PauseTimerService: Intent is pause "+intent?.getLongExtra(Constants.SERVICE_INTENT_DELAY_AMOUNT, time))
            AlarmHandler.removeRepeatingTimecheck(applicationContext)

            val ie = intent?.getLongExtra(Constants.SERVICE_INTENT_DELAY_AMOUNT, time)

            mTimer?.cancel()


            if(ie==time){
                if(mCurentLengthIndex>Constants.TIME_PAUSE_SERVICE_LENGTH_ARRAY.size-1){
                    mCurentLengthIndex=1
                }


                Log.e(Constants.APP_NAME, "PauseTimerService: No custom time set, autotime: "+ mCurentLengthIndex+" : "+ Constants.TIME_PAUSE_SERVICE_LENGTH_ARRAY[mCurentLengthIndex])

                time = Constants.TIME_PAUSE_SERVICE_LENGTH_ARRAY[mCurentLengthIndex].toLong()

                mCurentLengthIndex++
            }else{
                time = ie as Long
            }

            if(time>0){
                val pn = PauseNotification()
                startForeground(PauseNotification.NOTIFICATION_ID, pn.startNotification(applicationContext, getTimestampInProperLength(time)))
            }

            timer(time, this).start()



        }

        return super.onStartCommand(intent, flags, startId)

    }

    fun timer(milliseconds: Long, context: Context) : CountDownTimer{

        if(!AlarmHandler.checkIfNextAlarmExists(this)){
            Toast.makeText(this, "Restarted Regular Checking in (${getTimestampInProperLength(milliseconds)})", Toast.LENGTH_SHORT).show()
        }

        mTimerTimeInitial=milliseconds
        val timer = object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mIsRunning=true
                mTimerTimeLeft=millisUntilFinished
               // Log.e(Constants.APP_NAME, "PauseTimerService: Timer($seconds): running, ${millisUntilFinished/1000} left")
                for (interfaceElement in mListenerList){
                    //Log.e(Constants.APP_NAME, "PauseTimerService: Timer update interfaces")
                    interfaceElement.timerReduced(context, millisUntilFinished, getTimestampInProperLength(millisUntilFinished))
                }
            }

            override fun onFinish() {
                Log.e(Constants.APP_NAME, "PauseTimerService: Timer($milliseconds): ended, restarting checks!")
                AlarmHandler.createRepeatingTimecheck(applicationContext)
                for (interfaceElement in mListenerList){
                    interfaceElement.timerFinished(context)
                }
                resetTimerSystem()
            }
        }
        mTimer = timer
        return timer
    }

    private fun resetTimerSystem(){
        mIsRunning=false
        mTimerTimeLeft=-1
        mTimerTimeInitial=-1
        //reset autotimer when finished
        mCurentLengthIndex=0

        stopForeground(false)
        PauseNotification().cancelNotification(PauseNotification.NOTIFICATION_ID, this)
    }

}

