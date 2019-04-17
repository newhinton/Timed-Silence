package de.felixnuesse.timedsilence.handler

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.util.Log
import de.felixnuesse.timedsilence.Constants


class HeadsetHandler {
    companion object {
        //https://stackoverflow.com/questions/16395054/check-whether-headphones-are-plugged-in
        fun headphonesConnected(context: Context): Boolean {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

            var isConnected = false

            Log.e(Constants.APP_NAME, "HeadsetHandler: Checking devices")
            for (deviceInfo in audioManager!!.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {

                Log.e(Constants.APP_NAME, "HeadsetHandler: Devicetype: "+deviceInfo.type)

                when (deviceInfo.type) {
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> isConnected = true
                    AudioDeviceInfo.TYPE_WIRED_HEADSET -> isConnected = true
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> isConnected = true
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> isConnected = true
                }
            }
            Log.e(Constants.APP_NAME, "HeadsetHandler: Found Headset: $isConnected")
            return isConnected
        }
    }
}
