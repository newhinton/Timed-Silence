package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log

class NoisyBroadcastReciever : BroadcastReceiver(){

    companion object {
        private const val TAG = "NoisyBroadcastReciever"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            Log.e(TAG, "NoisyBroadcastReciever: Becoming Noisy! Checking Volume Again!")
            //VolumeCalculator(context).calculateAllAndApply()
        }
    }


}