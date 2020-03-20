package de.felixnuesse.timedsilence.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME

class BluetoothBroadcastReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(APP_NAME, "BluetoothBroadcastReciever: ${intent.action}")

        if (intent.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            Log.e(APP_NAME, "BluetoothBroadcastReciever: Device connected!")
        }

        if (intent.action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
            Log.e(APP_NAME, "BluetoothBroadcastReciever: Device disconnected!")
        }

    }


}