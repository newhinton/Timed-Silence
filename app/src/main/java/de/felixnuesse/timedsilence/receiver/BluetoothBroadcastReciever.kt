package de.felixnuesse.timedsilence.receiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.REASON_BLUETOOTH_CONNECTED
import de.felixnuesse.timedsilence.extensions.TAG
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.handler.trigger.Trigger
import de.felixnuesse.timedsilence.handler.volume.VolumeHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeState
import de.felixnuesse.timedsilence.volumestate.StateGenerator

class BluetoothBroadcastReciever : BroadcastReceiver(){

    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG(), "BluetoothBroadcastReciever: ${intent.action}")

        if (intent.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            Log.e(TAG(), "BluetoothBroadcastReciever: Device connected!")

            val bluetoothDevice: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE,BluetoothDevice::class.java)
            } else {
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }

            val address = bluetoothDevice?.address ?: ""

            HeadsetHandler.getPairedDevicesWithDatabaseState(context).forEach {
                if(address == it.address) {
                    var volumeHandler = VolumeHandler(context)
                    volumeHandler.ignoreMusicPlaying(true)

                    var state = VolumeState(it.volumeState)
                    state.setReason(REASON_BLUETOOTH_CONNECTED, it.alias)
                    var supposedState = StateGenerator(context).stateAt(System.currentTimeMillis())

                    if (supposedState.state > state.state) {
                        volumeHandler.setVolumeStateAndApply(state)
                    }

                }
            }
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
                Log.e(TAG(), "BluetoothBroadcastReciever: No next alarm scheduled, don't update!")
            }
        }
    }
}
