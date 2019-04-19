package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.handler.AlarmHandler
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler

class BootReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(APP_NAME, "BootReciever: Started Device!")
        val t = AlarmBroadcastReceiver()
        t.switchVolumeMode(context)

        val restartOnBoot= SharedPreferencesHandler.getPref(
            context,
            PrefConstants.PREF_BOOT_RESTART,
            PrefConstants.PREF_BOOT_RESTART_DEFAULT
        )

        if(restartOnBoot){
            Log.e(APP_NAME, "BootReciever: Started Checks!")
            AlarmHandler.createRepeatingTimecheck(context)
            return
        }
        Log.e(APP_NAME, "BootReciever: Dont check.")
    }


}