package de.felixnuesse.timedsilence.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeCalculator

class BluetoothBroadcastReciever : BroadcastReceiver(){

    companion object {
        private const val TAG = "BluetoothBroadcastReciever"
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG, "BluetoothBroadcastReciever: ${intent.action}")

        if (intent.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            Log.e(TAG, "BluetoothBroadcastReciever: Device connected!")
        }

        if (intent.action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
            Log.e(TAG, "BluetoothBroadcastReciever: Device disconnected!")

            try {
                Thread.sleep(1000)
            } catch (ex: InterruptedException) {
                Log.e(TAG, "BluetoothBroadcastReciever: Could not sleep!")
            }

            if(Trigger(context).checkIfNextAlarmExists()){
                var volCalculator = VolumeCalculator(context)
                volCalculator.ignoreMusicPlaying(true)
                volCalculator.calculateAllAndApply()
            } else {
                Log.e(TAG, "BluetoothBroadcastReciever: No next alarm scheduled, dont update!")
            }
        }
    }
}