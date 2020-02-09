package de.felixnuesse.timedsilence.handler.volume

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

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.provider.Settings
import androidx.core.app.ActivityCompat.finishAffinity
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.APP_NAME
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler

class VolumeHandler {
    companion object {
        fun getVolumePermission(context: Context) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (!notificationManager.isNotificationPolicyAccessGranted) {

                Log.d(Constants.APP_NAME, "VolumeHandler: Ask for DND-Access")

                val builder = AlertDialog.Builder(context)
                builder.setMessage(R.string.GrantDNDPermissionAccess)
                    .setPositiveButton(R.string.GrantDND,
                        DialogInterface.OnClickListener { dialog, id ->
                            val intent = Intent(
                                Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS
                            )
                            context.startActivity(intent)
                        })
                    .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            Log.e(Constants.APP_NAME, "VolumeHandler: Did not get 'Do not Disturb'-Access, quitting...")
                            finishAffinity(context as Activity)
                        })
                // Create the AlertDialog object and return it
                builder.create().show()
            }
        }
        fun hasVolumePermission(context: Context):Boolean{
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            return notificationManager.isNotificationPolicyAccessGranted
        }
    }

    var volumeSetting = TIME_SETTING_UNSET

    fun setSilent(){
        Log.d(Constants.APP_NAME, "VolumeHandler: Volume: Silent!")
        volumeSetting = TIME_SETTING_SILENT
    }

    fun setVibrate(){
        if(volumeSetting != TIME_SETTING_SILENT){
            Log.d(Constants.APP_NAME, "VolumeHandler: Volume: Vibrate!")
            volumeSetting = TIME_SETTING_VIBRATE
        }else{
            Log.d(Constants.APP_NAME, "VolumeHandler: Volume: Vibrate! Ignored because: $volumeSetting ")
        }
    }

    fun setLoud(){
        if(volumeSetting != TIME_SETTING_SILENT && volumeSetting != TIME_SETTING_VIBRATE){
            Log.d(Constants.APP_NAME, "VolumeHandler: Volume: Loud!")
            volumeSetting = TIME_SETTING_LOUD
        }else{
            Log.d(Constants.APP_NAME, "VolumeHandler: Volume: Loud! Ignored because: $volumeSetting ")
        }
    }

    private fun applySilent(context: Context) {
        Log.d(Constants.APP_NAME, "VolumeHandler: Apply: Silent!")
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
        mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)


        if(manager.ringerMode!= AudioManager.RINGER_MODE_SILENT){
            manager.ringerMode=AudioManager.RINGER_MODE_SILENT
        }


        if(!manager.isMusicActive){
            setMediaVolume(0, context, manager)
        }

        setStreamToPercent(
            manager,
            AudioManager.STREAM_ALARM,
            SharedPreferencesHandler.getPref(
                context,
                PrefConstants.PREF_VOLUME_ALARM,
                PrefConstants.PREF_VOLUME_ALARM_DEFAULT
            )
        )
        setStreamToPercent(
            manager,
            AudioManager.STREAM_NOTIFICATION,
            0
        )
        setStreamToPercent(
            manager,
            AudioManager.STREAM_RING,
            0
        )

    }

    private fun applyLoud(context: Context) {
        Log.d(Constants.APP_NAME, "VolumeHandler: Apply: Loud!")
    val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)

    if(manager.ringerMode!= AudioManager.RINGER_MODE_NORMAL){
        manager.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

    var alarmVolume= SharedPreferencesHandler.getPref(
        context,
        PrefConstants.PREF_VOLUME_ALARM,
        PrefConstants.PREF_VOLUME_ALARM_DEFAULT
    )
    var mediaVolume= SharedPreferencesHandler.getPref(
        context,
        PrefConstants.PREF_VOLUME_MUSIC,
        PrefConstants.PREF_VOLUME_MUSIC_DEFAULT
    )
    var notifcationVolume=
        SharedPreferencesHandler.getPref(
            context,
            PrefConstants.PREF_VOLUME_NOTIFICATION,
            PrefConstants.PREF_VOLUME_NOTIFICATION_DEFAULT
        )
    var ringerVolume=
        SharedPreferencesHandler.getPref(
            context,
            PrefConstants.PREF_VOLUME_RINGER,
            PrefConstants.PREF_VOLUME_RINGER_DEFAULT
        )


    if(!manager.isMusicActive){
        setMediaVolume(mediaVolume, context, manager)
    }

    setStreamToPercent(
        manager,
        AudioManager.STREAM_ALARM,
        alarmVolume
    )
    setStreamToPercent(
        manager,
        AudioManager.STREAM_NOTIFICATION,
        notifcationVolume
    )
    setStreamToPercent(
        manager,
        AudioManager.STREAM_RING,
        ringerVolume
    )

}

    private fun applyVibrate(context: Context) {
        Log.d(Constants.APP_NAME, "VolumeHandler: Apply: Vibrate!")
        val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        if(manager.ringerMode!= AudioManager.RINGER_MODE_VIBRATE){
            manager.ringerMode=AudioManager.RINGER_MODE_VIBRATE
        }


        val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)


        if(!manager.isMusicActive){
            setMediaVolume(0, context, manager)
        }



        var alarmVolume=
            SharedPreferencesHandler.getPref(
                context,
                PrefConstants.PREF_VOLUME_ALARM,
                PrefConstants.PREF_VOLUME_ALARM_DEFAULT
            )
        if(false){
            alarmVolume=0;
        }


        setStreamToPercent(
            manager,
            AudioManager.STREAM_ALARM,
            alarmVolume
        )
        setStreamToPercent(
            manager,
            AudioManager.STREAM_NOTIFICATION,
            0
        )
        setStreamToPercent(
            manager,
            AudioManager.STREAM_RING,
            0
        )

    }

    private fun setStreamToPercent(manager: AudioManager, stream: Int, percentage: Int) {
    val maxVol = manager.getStreamMaxVolume(stream)*100
    val onePercent = maxVol / 100
    val vol = (onePercent * percentage)/100
    manager.setStreamVolume(stream, vol, 0)
}

    private fun setMediaVolume(percentage: Int, context: Context, manager: AudioManager){
        setMediaVolume(percentage, context, manager, false)
    }

    private fun setMediaVolume(percentage: Int, context: Context, manager: AudioManager, ignoreHeadset: Boolean){


        Log.d(APP_NAME, "VolumeHandler: Setting Audio Volume!")

        val ignoreCheckWhenConnected=
            SharedPreferencesHandler.getPref(
                context,
                PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET,
                PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT
            )

        if(HeadsetHandler.headphonesConnected(context) && ignoreCheckWhenConnected){
            Log.d(APP_NAME, "VolumeHandler: Found headset, skipping...")
            return
        }

        setStreamToPercent(
            manager,
            AudioManager.STREAM_MUSIC,
            percentage
        )
        Log.d(APP_NAME, "VolumeHandler: Mediavolume set.")

    }

    fun isButtonClickAudible(context: Context): Boolean{
    Log.d(Constants.APP_NAME, "VolumeHandler: Check if Buttonclicks are audible")
    val manager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    if(0>=manager.getStreamVolume(AudioManager.STREAM_RING)){
        return false
    }
    return true
}

    fun applyVolume(context: Context){

        if(!hasVolumePermission(
                context
            )
        ){
            Log.d(APP_NAME, "VolumeHandler: VolumeSetting: Do not disturb not granted! Not changing Volume!")
            return
        }

        Log.d(Constants.APP_NAME, "VolumeHandler: VolumeSetting: $volumeSetting")

        when (volumeSetting) {
            TIME_SETTING_SILENT -> applySilent(context)
            TIME_SETTING_VIBRATE -> applyVibrate(context)
            TIME_SETTING_LOUD -> applyLoud(context)
            else -> {
                Log.d(Constants.APP_NAME, "VolumeHandler: Apply: Nothing, because no volume was selecteds!")
            }
        }
    }

    fun getVolume(): Int{
        when (volumeSetting) {
            TIME_SETTING_SILENT -> return TIME_SETTING_SILENT
            TIME_SETTING_VIBRATE -> return TIME_SETTING_VIBRATE
            TIME_SETTING_LOUD -> return TIME_SETTING_LOUD
            TIME_SETTING_UNSET -> return TIME_SETTING_UNSET
        }
        return TIME_SETTING_UNSET
    }
}