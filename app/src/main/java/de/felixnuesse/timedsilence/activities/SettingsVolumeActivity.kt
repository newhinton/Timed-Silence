package de.felixnuesse.timedsilence.activities

import android.content.Context
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.TextView
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import kotlinx.android.synthetic.main.activity_settings_main.*
import kotlinx.android.synthetic.main.activity_settings_volume.*

class SettingsVolumeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_volume)


        seekBarVolumeAlarm.max=100
        seekBarVolumeAlarm.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_ALARM, PrefConstants.PREF_VOLUME_ALARM_DEFAULT)
        updateTextViewPercentage(textViewPercentVolumeAlarm, seekBarVolumeAlarm.progress)
        seekBarVolumeAlarm?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(textViewPercentVolumeAlarm, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(textViewPercentVolumeAlarm, seekBar.progress)
                setAlarmVol(applicationContext, seekBar.progress)

            }
        })

        seekBarVolumeRinger.max=100
        seekBarVolumeRinger.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_RINGER, PrefConstants.PREF_VOLUME_RINGER_DEFAULT)
        updateTextViewPercentage(textViewPercentVolumeRinger, seekBarVolumeRinger.progress)
        seekBarVolumeRinger?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(textViewPercentVolumeRinger, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(textViewPercentVolumeRinger, seekBar.progress)
                setRingerVol(applicationContext, seekBar.progress)

            }
        })

        seekBarVolumeMusic.max=100
        seekBarVolumeMusic.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_MUSIC, PrefConstants.PREF_VOLUME_MUSIC_DEFAULT)
        updateTextViewPercentage(textViewPercentVolumeMusic, seekBarVolumeMusic.progress)
        seekBarVolumeMusic?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(textViewPercentVolumeMusic, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(textViewPercentVolumeMusic, seekBar.progress)
                setMusicVol(applicationContext, seekBar.progress)

            }
        })

        seekBarVolumeNotifications.max=100
        seekBarVolumeNotifications.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_NOTIFICATION, PrefConstants.PREF_VOLUME_NOTIFICATION_DEFAULT)
        updateTextViewPercentage(textViewPercentVolumeNotifications, seekBarVolumeNotifications.progress)
        seekBarVolumeNotifications?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(textViewPercentVolumeNotifications, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(textViewPercentVolumeNotifications, seekBar.progress)
                setNotificationVol(applicationContext, seekBar.progress)

            }
        })


    }

    fun updateTextViewPercentage(textView: TextView, percent: Int){
        textView.text= "$percent %"
        textView.setTextColor(textViewLabelSliderVolumeMusic.textColors)

        if(percent<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            textView.setTextColor(Color.RED)
        }

    }

    fun setAlarmVol(context: Context, value: Int) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_VOLUME_ALARM, value)
        Log.e(Constants.APP_NAME, "SettingsVolume: Setting Alarm Volume: "+value)
    }

    fun setRingerVol(context: Context, value: Int) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_VOLUME_RINGER, value)
        Log.e(Constants.APP_NAME, "SettingsVolume: Setting Ringer Volume: "+value)
    }

    fun setNotificationVol(context: Context, value: Int) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_VOLUME_NOTIFICATION, value)
        Log.e(Constants.APP_NAME, "SettingsVolume: Setting Notification Volume: "+value)
    }

    fun setMusicVol(context: Context, value: Int) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_VOLUME_MUSIC, value)
        Log.e(Constants.APP_NAME, "SettingsVolume: Setting Music Volume: "+value)
    }
}
