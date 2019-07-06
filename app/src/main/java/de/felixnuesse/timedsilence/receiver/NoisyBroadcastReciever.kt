package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME

class NoisyBroadcastReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            Log.e(APP_NAME, "NoisyBroadcastReciever: Becoming Noisy! Checking Volume Again!")
            AlarmBroadcastReceiver().switchVolumeMode(context)
        }
    }


}