package de.felixnuesse.timedsilence.handler.calculator

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.util.Log
import de.felixnuesse.timedsilence.Constants


class HeadsetHandler {
    companion object {

        private const val TAG = "HeadsetHandler"

        //https://stackoverflow.com/questions/16395054/check-whether-headphones-are-plugged-in
        fun headphonesConnected(context: Context): Boolean {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

            var isConnected = false

            Log.d(TAG, "HeadsetHandler: Checking devices")
            for (deviceInfo in audioManager!!.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {

                Log.d(TAG, "HeadsetHandler: Devicetype: "+deviceInfo.type)

                when (deviceInfo.type) {
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> isConnected = true
                    AudioDeviceInfo.TYPE_WIRED_HEADSET -> isConnected = true
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> isConnected = true
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> isConnected = true
                }
            }
            Log.d(TAG, "HeadsetHandler: Found Headset: $isConnected")
            return isConnected
        }
    }
}
