package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeCalculator
import de.felixnuesse.timedsilence.util.DateUtil
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmBroadcastReceiver"
    }

    override fun onReceive(context: Context, intent: Intent?) {

        val targetTime = intent?.getLongExtra(Constants.BROADCAST_INTENT_ACTION_TARGET_TIME, 0L)?: 0L
        // Precalculate, because writing to disk actually takes time. (In testing, 15ms)
        val diff = System.currentTimeMillis()-targetTime
        val duration = DateUtil.getDelta(targetTime, System.currentTimeMillis())

        LogHandler.writeLog(context,
            "AlarmBroadcastReceiver",
            "Alarm Recieved",
            "Was targeted at: $targetTime"
        )

        LogHandler.writeLog(context,
            "AlarmBroadcastReceiver",
            "Alarm Recieved",
            "Timediff: $duration ($diff ms)"
        )

        if(diff <= 0) {
            LogHandler.writeLog(context,
                "AlarmBroadcastReceiver",
                "Alarm Recieved",
                "Danger! We are before the scheduled time! (${diff*-1} ms before)"
            )
        }


        // Todo: fix this mess

        Log.e(TAG, "Alarmintent: Recieved Alarmintent at: ${DateUtil.getDate()}")

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME)) {
            Log.d(TAG, "Alarmintent: Content is to \"check the time\"")

            val sharedPref = context?.getSharedPreferences("test", Context.MODE_PRIVATE)
            with(sharedPref!!.edit()) {
                putLong("last_ExecTime", System.currentTimeMillis())
                apply()
            }


            switchVolumeMode(context)
            Trigger(context).createTimecheck()

        }

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_DELAY)) {

            val extra = intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION_DELAY_EXTRA)
            Log.d(TAG, "Alarmintent: Content is to \"$extra\"")

            if (extra.equals(Constants.BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW)) {
                Log.d(TAG, "Alarmintent: Content is to \"Restart recurring alarms\"")
                Trigger(context).createTimecheck()
            }

        }
    }

    @Deprecated("This is just a tiny useless wrapper. Please use 'VolumeCalculator(context).calculateAllAndApply()' directly")
    fun switchVolumeMode(context: Context?) {

        // copy is guaranteed to be to non-nullable whatever you do
        if (context == null) {
            Log.e(TAG, "Alarmintent: Error! Context invalid! Stopping!")
            return
        }

        VolumeCalculator(context).calculateAllAndApply()
    }


}