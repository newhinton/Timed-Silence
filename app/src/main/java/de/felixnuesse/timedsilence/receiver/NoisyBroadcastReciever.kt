package de.felixnuesse.timedsilence.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.volumestate.StateGenerator

class NoisyBroadcastReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            Log.e(TAG(), "NoisyBroadcastReciever: Becoming Noisy! Checking Volume Again!")
            VolumeHandler(context, "NoisyBroadcastReciever").setVolumeStateAndApply(StateGenerator(context).stateAt(System.currentTimeMillis()))
        }
    }


}