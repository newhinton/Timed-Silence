package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.trigger.Trigger

class BootReciever : BroadcastReceiver(){

    companion object {
        private const val TAG = "BootReciever"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action==Intent.ACTION_BOOT_COMPLETED){
            Log.e(TAG, "BootReciever: Started Device!")
            AlarmBroadcastReceiver().switchVolumeMode(context)

            val restartOnBoot= SharedPreferencesHandler.getPref(
                context,
                PrefConstants.PREF_BOOT_RESTART,
                PrefConstants.PREF_BOOT_RESTART_DEFAULT
            )

            if(restartOnBoot){
                Log.e(TAG, "BootReciever: Started Checks!")
                Trigger(context).createTimecheck()
                return
            }
            Log.e(TAG, "BootReciever: Dont check.")
        }
    }


}