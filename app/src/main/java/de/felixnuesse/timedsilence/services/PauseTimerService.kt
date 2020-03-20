package de.felixnuesse.timedsilence.services

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

import android.app.*
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import android.content.Context
import android.widget.Toast
import de.felixnuesse.timedsilence.handler.volume.AlarmHandler
import de.felixnuesse.timedsilence.services.`interface`.TimerInterface
import de.felixnuesse.timedsilence.ui.PauseNotification
import java.text.SimpleDateFormat
import java.util.*
import android.app.AlarmManager
import android.app.PendingIntent
import android.os.Handler
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.ui.LocationAccessMissingNotification



class PauseTimerService : Service() {

    companion object {

        var mCurentLengthIndex : Int = 0
        var mIsRunning: Boolean = false

        var mTimer: CountDownTimer? = null
        var mTimerStartTime: Long = -1
        var mTimerTimeLeft: Long = -1
        var mTimerTimeInitial: Long = -1

        private var mListenerList= arrayListOf<TimerInterface>()

        /**
         * This delay defines the intent that is fired to restart the service.
         * If we only use the countdowntimer, the service gets regularly killed by the OS
         */
        private val mCheckDelay: Long = (2*Constants.SEC).toLong()
        /**
         * This is used to define the length of the countdown timer that updates the ui
         * this needs to be uneven (uneven in seconds, not an uneven long)
         *  otherwise the timer may look like it sets out for one or two seconds
         */
        private val mCheckDelayUI: Long = (2*mCheckDelay) + Constants.SEC
        /**
         * This is used to define the length of the countdown timer. It ticks every mCheckInterval milliseconds
         */
        private val mCheckInterval: Long = (Constants.SEC).toLong()


        /**
         * This can be used to register a TimerInterface which can be used to update widgets/uielements with the current pause
         */
        fun registerListener(listener: TimerInterface){
            if(!mListenerList.contains(listener)){
                Log.e(APP_NAME,"PauseTimerService: mListenerList registered")
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
            Log.e(APP_NAME,"PauseTileService: service started")
            context.startForegroundService(i)
        }

        /**
         * This starts the autotimer. If it is already running, it cancels it and starts the next autotime in the array.
         */
        fun startTimer(context: Context, timeInMs: Long){
            val i =Intent(context, PauseTimerService::class.java)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_ACTION,Constants.SERVICE_INTENT_DELAY_ACTION)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_AMOUNT, timeInMs)
            Log.e(APP_NAME,"PauseTileService: service started with custom time")
            context.startForegroundService(i)
        }

        /**
         * This toggles the timer. If it is already running, it cancels it .
         */
        fun toggleTimer(context: Context, timeInMs: Long){
            val i =Intent(context, PauseTimerService::class.java)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_ACTION,Constants.SERVICE_INTENT_DELAY_ACTION_TOGGLE)
            i.putExtra(Constants.SERVICE_INTENT_DELAY_AMOUNT, timeInMs)
            Log.e(APP_NAME,"PauseTileService: service toggled with custom time")
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

            val intent = Intent(context, PauseTimerService::class.java)
            intent.putExtra(
                Constants.SERVICE_INTENT_DELAY_ACTION,
                Constants.SERVICE_INTENT_STOP_ACTION
            )
            context.startService(intent)

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

            var template = "mm:ss"
            if(timeAsLong>=Constants.HOUR){
                template = "HH:mm:ss"
            }
            format = SimpleDateFormat(template, Locale.US)

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

        if (intent?.getStringExtra(Constants.SERVICE_INTENT_DELAY_ACTION).equals(Constants.SERVICE_INTENT_STOP_ACTION)){
            mTimerTimeLeft=-1
        }

        if (intent?.getStringExtra(Constants.SERVICE_INTENT_DELAY_ACTION).equals(Constants.SERVICE_INTENT_TICK_ACTION)){
            Log.e(Constants.APP_NAME,"PauseTileService: service tick!")


            //if the timer was already canceled, stop right now
            if(mTimerTimeLeft<=0){
                finishTimer(this, mTimerTimeInitial)
                return super.onStartCommand(intent, flags, startId)
            }

            calcTime()

            if(mTimerTimeLeft>0){
                timerUiUpdater(mCheckDelayUI, this).start()
                tickTimer(this, mTimerTimeLeft)
                timerWakeServiceAgain(this)
            }else{
                finishTimer(this, mTimerTimeInitial)
            }

        }

        if (intent?.getStringExtra(Constants.SERVICE_INTENT_DELAY_ACTION).equals(Constants.SERVICE_INTENT_DELAY_ACTION_TOGGLE)){
            Log.e(Constants.APP_NAME,"PauseTileService: service toggled")
            if(mIsRunning){
                finishTimer(this)
                resetTimerSystem(this)
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


            //timer(time, this).start()

            mTimerTimeLeft = time
            mTimerTimeInitial = time
            mTimerStartTime = now()

            if(!AlarmHandler.checkIfNextAlarmExists(this)){
                Toast.makeText(this, "Restarted Regular Checking in (${getTimestampInProperLength(mTimerTimeInitial)})", Toast.LENGTH_SHORT).show()
            }

            Log.e("test", "start?")

            timerWakeServiceAgain(this)
            timerUiUpdater(mCheckDelayUI, this).start()

        }

        return super.onStartCommand(intent, flags, startId)

    }


    fun now(): Long{
        val currentTime = Calendar.getInstance().time
        return currentTime.time
    }


    private fun tickTimer(context: Context, millisUntilFinished: Long){
        mIsRunning=true
        mTimerTimeLeft=millisUntilFinished

        var nonnullms=millisUntilFinished
        if(millisUntilFinished<0){
            nonnullms=0
        }
        // Log.e(Constants.APP_NAME, "PauseTimerService: Timer($seconds): running, ${millisUntilFinished/1000} left")
        for (interfaceElement in mListenerList){
            //Log.e(Constants.APP_NAME, "PauseTimerService: Timer update interfaces")
            interfaceElement.timerReduced(context, nonnullms, getTimestampInProperLength(nonnullms))
        }
    }

    private fun finishTimer(context: Context, milliseconds: Long){
        Log.e(APP_NAME, "PauseTimerService: Timer($milliseconds): ended, restarting checks!")

        AlarmHandler.createRepeatingTimecheck(applicationContext)
        for (interfaceElement in mListenerList){
            interfaceElement.timerFinished(context)
        }
        resetTimerSystem(context)
    }



    fun resetTimerSystem(context: Context){
        mIsRunning=false
        mTimerTimeLeft=-1
        mTimerTimeInitial=-1
        mTimerStartTime=0
        //reset autotimer when finished
        mCurentLengthIndex=0

        //remove UI afterwards. Only gets removed if thme left is 0 or smaller
        mTimer?.cancel()

        Log.e(APP_NAME, "PauseTimerService: resetTimerSystem: Exit!")


        stopForeground(false)
        Handler().postDelayed({
            PauseNotification.cancelNotification(context)
        }, 500)


    }

    fun timerUiUpdater(milliseconds: Long, context: Context) : CountDownTimer{

        LocationAccessMissingNotification.cancelNotification(context)

        if(mTimer!=null){
            Log.e(APP_NAME, "PauseTimerService: timerUiUpdater(): Cancel existing timer!")
            mTimer!!.cancel()
        }
        mTimer = object : CountDownTimer(milliseconds, mCheckInterval) {
            override fun onTick(millisUntilFinished: Long) {
                if(mTimerTimeLeft>0){
                    calcTime()
                    tickTimer(context, mTimerTimeLeft)
                }
            }

            override fun onFinish() {
                //never do something here, since this countdown only updates the ui, .cancel() or anything like it will immediatly stop the pause after $milliseconds
                //DONT: finishTimer(context, milliseconds)
                calcTime()
                if(mTimerTimeLeft<=0){
                    finishTimer(context, mTimerTimeInitial)
                }
            }
        }
        return mTimer as CountDownTimer
    }

    fun calcTime(){
        val now = now()
        val sinceStart =  now-mTimerStartTime
        mTimerTimeLeft=mTimerTimeInitial-sinceStart
    }


    private fun timerAlertPendingIntent(context: Context):PendingIntent{
        val intent = Intent(context, PauseTimerService::class.java)
        intent.putExtra(
            Constants.SERVICE_INTENT_DELAY_ACTION,
            Constants.SERVICE_INTENT_TICK_ACTION
        )

        val pintent = PendingIntent.getService(context, 0, intent, 0)
        return pintent
    }

    fun timerWakeServiceAgain(context: Context){
        val alarm = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+mCheckDelay, timerAlertPendingIntent(context))

    }

}

