package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeCalculator
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "AlarmBroadcastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        //todo: fix this mess
        if (context != null) {
            LogHandler.writeAppLog(context,"Alarmintent: Recieved Alarmintent")
        }
        val current = System.currentTimeMillis()
        val date = Date(current)
        val dateFormat = android.text.format.DateFormat.getDateFormat(context)
        val currentformatted = dateFormat.format(date)

        Log.e(TAG, "Alarmintent: Recieved Alarmintent at: $currentformatted")

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_UPDATE_VOLUME)) {
            Log.d(TAG, "Alarmintent: Content is to \"check the time\"")

            val sharedPref = context?.getSharedPreferences("test", Context.MODE_PRIVATE)
            with(sharedPref!!.edit()) {
                putLong("last_ExecTime", current)
                apply()
            }


            switchVolumeMode(context)
            Trigger(context).createTimecheck()

        }

        if (intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION).equals(Constants.BROADCAST_INTENT_ACTION_DELAY)) {

            val extra = intent?.getStringExtra(Constants.BROADCAST_INTENT_ACTION_DELAY_EXTRA)
            Log.d(TAG, "Alarmintent: Content is to \"" + extra + "\"")

            if (extra.equals(Constants.BROADCAST_INTENT_ACTION_DELAY_RESTART_NOW)) {
                Log.d(TAG, "Alarmintent: Content is to \"Restart recurring alarms\"")
                Trigger(context!!).createTimecheck()
            }

        }
    }

    @Deprecated("This is just a tiny useless wrapper. Please use 'VolumeCalculator(context).calculateAllAndApply()' directly")
    fun switchVolumeMode(context: Context?) {

        val nonNullContext = context
        // copy is guaranteed to be to non-nullable whatever you do
        if (nonNullContext == null) {
            Log.e(TAG, "Alarmintent: Error! Context invalid! Stopping!")
            return
        }

        VolumeCalculator(nonNullContext).calculateAllAndApply()
    }


}