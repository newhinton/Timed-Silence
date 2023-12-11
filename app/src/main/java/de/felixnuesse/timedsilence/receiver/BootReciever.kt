package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.volumestate.StateGenerator

class BootReciever : BroadcastReceiver(){

    companion object {
        private const val TAG = "BootReciever"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if(intent.action==Intent.ACTION_BOOT_COMPLETED){
            Log.e(TAG, "BootReciever: Started Device!")
            VolumeHandler(context).setVolumeStateAndApply(StateGenerator(context).stateAt(System.currentTimeMillis()))

            if(PreferencesManager(context).shouldRestartOnBoot()){
                Log.e(TAG, "BootReciever: Started Checks!")
                Trigger(context).createTimecheck()
                return
            }
            Log.e(TAG, "BootReciever: Dont check.")
        }
    }


}