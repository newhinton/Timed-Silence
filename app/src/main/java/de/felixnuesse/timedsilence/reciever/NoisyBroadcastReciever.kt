package de.felixnuesse.timedsilence.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager

class NoisyBroadcastReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            // Pause the playback
        }
    }


}