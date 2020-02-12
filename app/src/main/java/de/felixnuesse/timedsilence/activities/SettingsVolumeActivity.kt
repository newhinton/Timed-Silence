package de.felixnuesse.timedsilence.activities

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.ThemeHandler
import kotlinx.android.synthetic.main.activity_settings_volume.*
import androidx.core.app.NavUtils
import android.view.MenuItem


class SettingsVolumeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemeHandler.setTheme(this, window)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_settings_volume)

        //sets the actionbartitle
        title = resources.getString(R.string.actionbar_title_settings_volume)

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

        checkWarningVisibility()

    }

    @SuppressLint("SetTextI18n")
    fun updateTextViewPercentage(textView: TextView, percent: Int){
        textView.text= "$percent %"
        textView.setTextColor(textViewLabelSliderVolumeMusic.textColors)

        if(percent<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            textView.setTextColor(Color.RED)
        }
        checkWarningVisibility()
    }

    fun checkWarningVisibility(){
        warning_low_volume.visibility = View.INVISIBLE

        var warning_show=false

        if(seekBarVolumeAlarm.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }
        if(seekBarVolumeMusic.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }
        if(seekBarVolumeNotifications.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }
        if(seekBarVolumeRinger.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }

        if(warning_show){
            warning_low_volume.visibility = View.VISIBLE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
