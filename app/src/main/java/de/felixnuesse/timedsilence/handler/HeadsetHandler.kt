package de.felixnuesse.timedsilence.handler

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager



class HeadsetHandler {

    private fun isHeadphonesPlugged(context: Context): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
        val audioDevices = audioManager!!.getDevices(AudioManager.GET_DEVICES_ALL)
        for (deviceInfo in audioDevices) {
            if (deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADPHONES || deviceInfo.type == AudioDeviceInfo.TYPE_WIRED_HEADSET) {
                return true
            }
        }
        return false
    }

}
