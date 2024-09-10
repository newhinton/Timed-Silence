package de.felixnuesse.timedsilence.util

import android.content.Context
import android.os.Build
import android.os.Vibrator
import android.os.VibratorManager


class VibrationUtil {

    companion object {
        fun canVibrate(context: Context): Boolean {
            val v = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibrationService = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibrationService.defaultVibrator
            } else {
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            return v.hasVibrator()
        }
    }
}