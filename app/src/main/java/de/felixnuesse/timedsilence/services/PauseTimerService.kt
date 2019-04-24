package de.felixnuesse.timedsilence.services;

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import android.R.string.cancel
import android.app.AlarmManager
import android.content.Context
import android.content.Context.VIBRATOR_SERVICE
import android.support.v4.content.ContextCompat.getSystemService
import android.os.Vibrator
import android.widget.Toast
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface


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

        fun finishTimer(context: Context){

            AlarmHandler.createRepeatingTimecheck(context)
            for (interfaceElement in mListenerList){
                interfaceElement.timerFinished()
            }

            mTimer!!.cancel()
            mIsRunning=false
        }

        fun cancelTimer(){

            for (interfaceElement in mListenerList){
                interfaceElement.timerFinished()
            }

            mTimer!!.cancel()
            mIsRunning=false
        }

    }

    override fun onBind(intent: Intent?): IBinder? {

        //super.onBind(intent)
        return null

    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        //super.onBind(intent)
        Log.e(Constants.APP_NAME, "PauseTimerService: Intent is called")

        if (intent?.getStringExtra(Constants.SERVICE_INTENT_DELAY_ACTION).equals(Constants.SERVICE_INTENT_DELAY_ACTION)){
            Log.e(Constants.APP_NAME, "PauseTimerService: Intent is pause "+intent?.getStringExtra(Constants.SERVICE_INTENT_DELAY_AMOUNT))
            AlarmHandler.removeRepeatingTimecheck(applicationContext)

            var time: Long= -1
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
            timer(time).start()



        }

        return super.onStartCommand(intent, flags, startId)

    }

    fun timer(milliseconds: Long) : CountDownTimer{

        if(!AlarmHandler.checkIfNextAlarmExists(this)){
            Toast.makeText(this, "Restarted Regular Checking in (${PauseTileService.getTimestampInProperLength(milliseconds)})", Toast.LENGTH_SHORT).show()
        }

        val timer = object : CountDownTimer(milliseconds, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                mIsRunning=true
               // Log.e(Constants.APP_NAME, "PauseTimerService: Timer($seconds): running, ${millisUntilFinished/1000} left")
                for (interfaceElement in mListenerList){
                    //Log.e(Constants.APP_NAME, "PauseTimerService: Timer update interfaces")
                    interfaceElement.timerReduced(millisUntilFinished)
                }
            }

            override fun onFinish() {
                Log.e(Constants.APP_NAME, "PauseTimerService: Timer($milliseconds): ended, restarting checks!")
                AlarmHandler.createRepeatingTimecheck(applicationContext)
                for (interfaceElement in mListenerList){
                    interfaceElement.timerFinished()
                }
                mIsRunning=false
            }
        }
        mTimer = timer
        return timer
    }


}

