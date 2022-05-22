package de.felixnuesse.timedsilence.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.handler.trigger.TargetedAlarmHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeCalculator

class BluetoothBroadcastReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(APP_NAME, "BluetoothBroadcastReciever: ${intent.action}")

        if (intent.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            Log.e(APP_NAME, "BluetoothBroadcastReciever: Device connected!")
        }

        if (intent.action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
            Log.e(APP_NAME, "BluetoothBroadcastReciever: Device disconnected!")

            try {
                Thread.sleep(1000)
            } catch (ex: InterruptedException) {
                Log.e(APP_NAME, "BluetoothBroadcastReciever: Could not sleep!")
            }

            if(TargetedAlarmHandler(context).checkIfNextAlarmExists()){
                var volCalculator = VolumeCalculator(context)
                volCalculator.ignoreMusicPlaying(true)
                volCalculator.calculateAllAndApply()
            } else {
                Log.e(APP_NAME, "BluetoothBroadcastReciever: No next alarm scheduled, dont update!")
            }
        }

    }


}