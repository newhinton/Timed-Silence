package de.felixnuesse.timedsilence

/**
 * Copyright (C) 2019  Felix Nüsse
 * Created on 10.04.19 - 18:07
 *
 * Edited by: Felix Nüsse felix.nuesse(at)t-online.de
 *
 *
 * This program is released under the GPLv3 license
 *
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA
 *
 *
 *
 */

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
                    Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                )
                context.startActivity(intent)
            }
        }


        fun setSilent(context: Context) {

            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
            mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)

            if(manager.ringerMode!= AudioManager.RINGER_MODE_SILENT){
                manager.ringerMode=AudioManager.RINGER_MODE_SILENT
            }

            setStreamToPercent(manager, AudioManager.STREAM_MUSIC, 0)
            setStreamToPercent(manager, AudioManager.STREAM_ALARM, 70)
            setStreamToPercent(manager, AudioManager.STREAM_NOTIFICATION, 0)
            setStreamToPercent(manager, AudioManager.STREAM_RING, 0)




        }

        fun setLoud(context: Context) {

            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager


            if(manager.ringerMode!= AudioManager.RINGER_MODE_NORMAL){
                manager.setRingerMode(AudioManager.RINGER_MODE_NORMAL)
            }


            setStreamToPercent(manager, AudioManager.STREAM_MUSIC, 80)
            setStreamToPercent(manager, AudioManager.STREAM_ALARM, 80)
            setStreamToPercent(manager, AudioManager.STREAM_NOTIFICATION, 80)
            setStreamToPercent(manager, AudioManager.STREAM_RING, 80)


        }

        fun setVibrate(context: Context) {

            val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            if(manager.ringerMode!= AudioManager.RINGER_MODE_VIBRATE){
                manager.ringerMode=AudioManager.RINGER_MODE_VIBRATE
            }

            setStreamToPercent(manager, AudioManager.STREAM_MUSIC, 0)
            setStreamToPercent(manager, AudioManager.STREAM_ALARM, 0)
            setStreamToPercent(manager, AudioManager.STREAM_NOTIFICATION, 0)
            setStreamToPercent(manager, AudioManager.STREAM_RING, 0)

        }

        fun setStreamToPercent(manager: AudioManager, stream: Int, percentage: Int) {
            val maxVol = manager.getStreamMaxVolume(stream)*100
            val onePercent = maxVol / 100
            val vol = (onePercent * percentage)/100
            manager.setStreamVolume(stream, vol, 0)
        }

    }
}