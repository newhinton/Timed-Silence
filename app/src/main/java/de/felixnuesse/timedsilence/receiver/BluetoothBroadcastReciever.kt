package de.felixnuesse.timedsilence.receiver

import android.annotation.SuppressLint
import android.bluetooth.BluetoothClass.Device.Major.AUDIO_VIDEO
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
import de.felixnuesse.timedsilence.util.PermissionManager
import de.felixnuesse.timedsilence.volumestate.StateGenerator

class BluetoothBroadcastReciever : BroadcastReceiver(){

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        Log.e(TAG(), "BluetoothBroadcastReciever: ${intent.action}")

        if (intent.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            Log.e(TAG(), "BluetoothBroadcastReciever: Device connected!")

            val bluetoothDevice: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
            } else {
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }

            val address = bluetoothDevice?.address ?: ""

            HeadsetHandler.getPairedDevicesWithDatabaseState(context).forEach {
                if(address == it.address) {
                    val volumeHandler = VolumeHandler(context, "BluetoothBroadcastReciever")
                    volumeHandler.ignoreMusicPlaying(true)

                    val state = VolumeState(it.volumeState)
                    state.setReason(REASON_BLUETOOTH_CONNECTED, it.alias)
                    val supposedState = StateGenerator(context).stateAt(System.currentTimeMillis())

                    if (supposedState.state > state.state) {
                        volumeHandler.setVolumeStateAndApply(state)
                    }

                }
            }
        }

        if (intent.action == BluetoothDevice.ACTION_ACL_DISCONNECTED) {

            val allowed = PermissionManager(context).grantedBluetoothAccess()
            val device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
            val name = if (device != null && allowed) {
                device.name
            } else {
                "Unknown!"
            }

            Log.e(TAG(), "BluetoothBroadcastReciever: Device disconnected: $name!")

            var isAudioDevice = false
            if(allowed) {
                if(device?.bluetoothClass?.majorDeviceClass == AUDIO_VIDEO) {
                    isAudioDevice = true
                }
            }

            try {
                // Why do we sleep here? So that the device is gone when we check?
                Thread.sleep(1000)
            } catch (ex: InterruptedException) {
                Log.e(TAG(), "BluetoothBroadcastReciever: Could not sleep!")
            }

            if(Trigger(context).checkIfNextAlarmExists()){
                var volumeHandler = VolumeHandler(context, "BluetoothBroadcastReciever")
                // is this what we want? Override existing playing content?
                // yes, but only if the device we are loosing are headphones.
                // Todo: Check if headphones disconnected!
                volumeHandler.ignoreMusicPlaying(isAudioDevice)
                volumeHandler.setVolumeStateAndApply(StateGenerator(context).stateAt(System.currentTimeMillis()))
            } else {
                Log.e(TAG(), "BluetoothBroadcastReciever: No next alarm scheduled, don't update!")
            }
        }
    }
}
