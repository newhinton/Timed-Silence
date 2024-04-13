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

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.util.Log
import de.felixnuesse.timedsilence.Constants.Companion.REASON_MANUALLY_SET
import de.felixnuesse.timedsilence.handler.LogHandler
import de.felixnuesse.timedsilence.handler.PreferencesManager
import de.felixnuesse.timedsilence.handler.calculator.HeadsetHandler
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.handler.volume.VolumeState.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.util.PermissionManager
import de.felixnuesse.timedsilence.extensions.TAG

class VolumeHandler(private var mContext: Context) {

    private var mPreferencesManager = PreferencesManager(mContext)
    private var volumeState = VolumeState(PreferencesManager(mContext).getDefaultUnsetVolume())
    private var mIgnoreMusicPlaying = false

    fun setVolumeStateAndApply(state: VolumeState) {
        setVolumeState(state)
        applyVolume()
    }
    fun setVolumeState(state: VolumeState) {
        volumeState = state
    }

    fun setSilent(){
        volumeState = VolumeState(TIME_SETTING_SILENT)
        volumeState.setReason(REASON_MANUALLY_SET, "Set from Main View")
    }

    fun setVibrate(){
        volumeState = VolumeState(TIME_SETTING_VIBRATE)
        volumeState.setReason(REASON_MANUALLY_SET, "Set from Main View")
    }

    fun setLoud(){
        volumeState = VolumeState(TIME_SETTING_LOUD)
        volumeState.setReason(REASON_MANUALLY_SET, "Set from Main View")
    }

    fun ignoreMusicPlaying(ignore: Boolean) {
        mIgnoreMusicPlaying = ignore
    }

    private fun applySilent() {
        Log.e(TAG(), "VolumeHandler: Apply: Silent!")
        val manager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager


        if(!manager.isMusicActive || mIgnoreMusicPlaying){
            setMediaVolume(0, manager)
        }

        setStreamToPercent(
            manager,
            AudioManager.STREAM_ALARM,
            PreferencesManager(mContext).getAlarmVolume()
        )



        if(mPreferencesManager.changeRingerVolume()){
            Log.d(TAG(), "VolumeHandler: Setting Ringer! This might be not what you want!")
            setStreamToPercent(
                manager,
                AudioManager.STREAM_RING,
                0
            )
            if(manager.ringerMode!= AudioManager.RINGER_MODE_SILENT){
                manager.ringerMode=AudioManager.RINGER_MODE_SILENT
            }
        }


        val mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALARMS)
        mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)

    }

    private fun applyLoud() {
        Log.d(TAG(), "VolumeHandler: Apply: Loud!")
        val manager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)

        if(manager.ringerMode!= AudioManager.RINGER_MODE_NORMAL){
            manager.ringerMode = AudioManager.RINGER_MODE_NORMAL
        }

        val alarmVolume = mPreferencesManager.getAlarmVolume()
        val mediaVolume = mPreferencesManager.getMediaVolume()
        val notifcationVolume = mPreferencesManager.getNotificationVolume()
        val ringerVolume = mPreferencesManager.getRingerVolume()

        if(!manager.isMusicActive){
            setMediaVolume(mediaVolume, manager)
        }

        Log.d(TAG(), "VolumeHandler: STREAM_MEDIA: $mediaVolume")
        Log.d(TAG(), "VolumeHandler: STREAM_ALARM: $alarmVolume")
        Log.d(TAG(), "VolumeHandler: STREAM_NOTIFICATION: $notifcationVolume")


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

        if(mPreferencesManager.changeRingerVolume()){
            Log.d(TAG(), "VolumeHandler: STREAM_RING: $ringerVolume")
            Log.d(TAG(), "VolumeHandler: Setting Ringer! This might be not what you want!")
            setStreamToPercent(
                manager,
                AudioManager.STREAM_RING,
                ringerVolume
            )
        }

    }

    private fun applyVibrate() {
        Log.d(TAG(), "VolumeHandler: Apply: Vibrate!")
        val manager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        val mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
        mNotificationManager?.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)


        if(!manager.isMusicActive || mIgnoreMusicPlaying){
            setMediaVolume(0, manager)
        }

        if(manager.ringerMode!= AudioManager.RINGER_MODE_VIBRATE){
            manager.ringerMode=AudioManager.RINGER_MODE_VIBRATE
        }

        var alarmVolume = mPreferencesManager.getAlarmVolume()

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

        if(mPreferencesManager.changeRingerVolume()){
            Log.d(TAG(), "VolumeHandler: Silencing Ringer! This might be not what you want!")
            setStreamToPercent(
                manager,
                AudioManager.STREAM_RING,
                0
            )
        }
    }

    private fun setStreamToPercent(manager: AudioManager, stream: Int, percentage: Int) {
        val maxVol = manager.getStreamMaxVolume(stream)*100
        val onePercent = maxVol / 100
        val vol = (onePercent * percentage)/100
        manager.setStreamVolume(stream, vol, 0)
    }

    private fun setMediaVolume(percentage: Int, manager: AudioManager){

        Log.d(TAG(), "VolumeHandler: Setting Audio Volume!")
        val ignoreCheckWhenConnected = mPreferencesManager.checkIfHeadsetIsConnected()

        if(HeadsetHandler.headphonesConnected(mContext) && ignoreCheckWhenConnected){
            Log.d(TAG(), "VolumeHandler: Found headset, skipping...")
            return
        }

        setStreamToPercent(
            manager,
            AudioManager.STREAM_MUSIC,
            percentage
        )
        Log.d(TAG(), "VolumeHandler: Mediavolume set.")
    }

    fun isButtonClickAudible(): Boolean{
        Log.d(TAG(), "VolumeHandler: Check if Buttonclicks are audible")
        val manager = mContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if(0>=manager.getStreamVolume(AudioManager.STREAM_RING)){
            return false
        }
        return true
    }

    fun applyVolume(){
        if(!PermissionManager(mContext).grantedDoNotDisturbAndNotify()){
            Log.d(TAG(), "VolumeHandler: VolumeSetting: Do not disturb not granted! Not changing Volume!")
            return
        }

        Log.d(TAG(), "VolumeHandler: VolumeSetting: ${getVolume()}")
        LogHandler.writeLog(mContext,"VolumeHandler", "because applyVolume was called","${VolumeState.timeSettingToReadable(getVolume())} - ${volumeState.getReason()}")

        when (getVolume()) {
            TIME_SETTING_SILENT -> {
                applySilent()
                LogHandler.writeLog(mContext,"VolumeHandler", "because applyVolume was called","${VolumeState.timeSettingToReadable(TIME_SETTING_SILENT)} - ${volumeState.getReason()}")
            }
            TIME_SETTING_VIBRATE -> {
                applyVibrate()
                LogHandler.writeLog(mContext,"VolumeHandler", "because applyVolume was called","${VolumeState.timeSettingToReadable(TIME_SETTING_VIBRATE)} - ${volumeState.getReason()}")
            }
            TIME_SETTING_LOUD -> {
                applyLoud()
                LogHandler.writeLog(mContext,"VolumeHandler", "because applyVolume was called","${VolumeState.timeSettingToReadable(TIME_SETTING_LOUD)} - ${volumeState.getReason()}")
            }
            else -> {
                Log.d(TAG(), "VolumeHandler: Apply: Nothing, because no volume was selecteds!")
                LogHandler.writeLog(mContext,"VolumeHandler", "because applyVolume was called","Nothing, because no volume was selected!")
            }
        }
    }

    private fun getVolume(): Int{
        when (volumeState.state) {
            TIME_SETTING_SILENT -> return TIME_SETTING_SILENT
            TIME_SETTING_VIBRATE -> return TIME_SETTING_VIBRATE
            TIME_SETTING_LOUD -> return TIME_SETTING_LOUD
            TIME_SETTING_UNSET -> return TIME_SETTING_UNSET
        }

        return TIME_SETTING_UNSET
    }
}