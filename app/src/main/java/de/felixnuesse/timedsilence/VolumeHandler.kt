package de.felixnuesse.timedsilence

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings

class VolumeHandler {
    companion object {

        fun getVolumePermission(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                val intent = Intent(
                    Settings
                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                )
                context.startActivity(intent)
            }
        }


        fun setSilent(context: Context) {

            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            setStreamToPercent(manager, AudioManager.STREAM_MUSIC, 0)
            setStreamToPercent(manager, AudioManager.STREAM_ALARM, 0)
            setStreamToPercent(manager, AudioManager.STREAM_NOTIFICATION, 0)
            setStreamToPercent(manager, AudioManager.STREAM_RING, 0)
            manager.setRingerMode(AudioManager.RINGER_MODE_SILENT)

        }

        fun setLoud(context: Context) {

            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            manager.setStreamVolume(AudioManager.STREAM_MUSIC, manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0)
            manager.setStreamVolume(AudioManager.STREAM_ALARM, manager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0)
            manager.setStreamVolume(AudioManager.STREAM_RING, manager.getStreamMaxVolume(AudioManager.STREAM_RING), 0)
            manager.setStreamVolume(
                AudioManager.STREAM_NOTIFICATION,
                manager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION),
                0
            )
            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)

        }

        fun setVibrate(context: Context) {

            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
            setStreamToPercent(manager, AudioManager.STREAM_MUSIC, 0)
            setStreamToPercent(manager, AudioManager.STREAM_ALARM, 0)
            setStreamToPercent(manager, AudioManager.STREAM_NOTIFICATION, 0)
            setStreamToPercent(manager, AudioManager.STREAM_RING, 0)

        }

        fun setStreamToPercent(manager: AudioManager, stream: Int, percentage: Int) {

            val maxVol = manager.getStreamMaxVolume(stream)
            val onePercent = maxVol / 100
            val vol = onePercent * percentage
            manager.setStreamVolume(stream, vol, 0);
        }

    }
}