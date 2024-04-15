package de.felixnuesse.timedsilence.util

import android.content.Context
import android.content.pm.PackageManager

class BluetoothUtil {
    companion object {
        fun hasBluetooth(context: Context): Boolean {
            return context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        }
    }
}