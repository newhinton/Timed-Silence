package de.felixnuesse.timedsilence.handler.calculator

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log
import de.felixnuesse.timedsilence.model.data.BluetoothObject
import de.felixnuesse.timedsilence.model.database.DatabaseHandler
import de.felixnuesse.timedsilence.util.PermissionManager
import java.lang.reflect.Method


class HeadsetHandler {
    companion object {

        private const val TAG = "BluetoothHandler"

        //https://stackoverflow.com/questions/16395054/check-whether-headphones-are-plugged-in
        fun headphonesConnected(context: Context): Boolean {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?

            var isConnected = false

            Log.d(TAG, "Checking devices")
            for (deviceInfo in audioManager!!.getDevices(AudioManager.GET_DEVICES_OUTPUTS)) {

                Log.d(TAG, "Devicetype: "+deviceInfo.type)

                when (deviceInfo.type) {
                    AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> isConnected = true
                    AudioDeviceInfo.TYPE_WIRED_HEADSET -> isConnected = true
                    AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> isConnected = true
                    AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> isConnected = true
                }
            }
            Log.d(TAG, "Found Headset: $isConnected")
            return isConnected
        }


        @SuppressLint("MissingPermission") // Handled by PermissionManager check
        fun getPairedDevices(context: Context): ArrayList<BluetoothObject> {
            val list = arrayListOf<BluetoothObject>()

            Log.e(TAG, "paired dev")
            if(!PermissionManager(context).grantedBluetoothAccess()) {
                Log.e(TAG, "no perm")
                PermissionManager(context).requestBluetooth()
                return list
            }

            val btManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
            //todo: Fix this. When the device does not have bluetooth, the app crashes
            if(btManager.adapter == null){
                return arrayListOf()
            }
            val pairedDevices = btManager.adapter.bondedDevices


            Log.e(TAG, "paired dev ${pairedDevices.size}")
            if (pairedDevices.size > 0) {

                for (device in pairedDevices) {
                    val deviceName = device.name
                    val macAddress = device.address
                    val aliasing = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        device.alias?: deviceName
                    } else {
                        deviceName
                    }

                    val bluetoothObject = BluetoothObject(deviceName, macAddress, aliasing, isConnected(device))
                    list.add(bluetoothObject)
                    Log.e(TAG,"paired device: $bluetoothObject")
                }
            }
            return list
        }

        fun getPairedDevicesWithDatabaseState(context: Context): ArrayList<BluetoothObject> {

            var db = DatabaseHandler(context)
            var result = arrayListOf<BluetoothObject>()

            getPairedDevices(context).forEach { bl ->
                val found = db.getBluetoothEntries().find { it.address ==  bl.address}
                bl.volumeState = found?.volumeState ?: bl.volumeState
                result.add(bl)
            }

            return result
        }

        private fun isConnected(device: BluetoothDevice): Boolean {
            return try {
                val m: Method = device.javaClass.getMethod("isConnected")
                m.invoke(device) as Boolean
            } catch (e: Exception) {
                throw IllegalStateException(e)
            }
        }

    }
}
