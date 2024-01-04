package de.felixnuesse.timedsilence.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.volumestate.StateGenerator

class BluetoothBroadcastReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG(), "BluetoothBroadcastReciever: ${intent.action}")

        if (intent.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            Log.e(TAG(), "BluetoothBroadcastReciever: Device connected!")
        }

        if (intent.action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
            Log.e(TAG(), "BluetoothBroadcastReciever: Device disconnected!")

            try {
                Thread.sleep(1000)
            } catch (ex: InterruptedException) {
                Log.e(TAG(), "BluetoothBroadcastReciever: Could not sleep!")
            }

            if(Trigger(context).checkIfNextAlarmExists()){
                var volumeHandler = VolumeHandler(context)
                volumeHandler.ignoreMusicPlaying(true)
                volumeHandler.setVolumeStateAndApply(StateGenerator(context).stateAt(System.currentTimeMillis()))
            } else {
                Log.e(TAG(), "BluetoothBroadcastReciever: No next alarm scheduled, dont update!")
            }
        }
    }
}