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
import android.view.MenuItem
import de.felixnuesse.timedsilence.databinding.ActivitySettingsVolumeBinding


class SettingsVolumeActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsVolumeBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsVolumeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        //sets the actionbartitle
        title = resources.getString(R.string.actionbar_title_settings_volume)

        binding.seekBarVolumeAlarm.max=100
        binding.seekBarVolumeAlarm.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_ALARM, PrefConstants.PREF_VOLUME_ALARM_DEFAULT)
        updateTextViewPercentage(binding.textViewPercentVolumeAlarm, binding.seekBarVolumeAlarm.progress)
        binding.seekBarVolumeAlarm.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(binding.textViewPercentVolumeAlarm, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(binding.textViewPercentVolumeAlarm, seekBar.progress)
                setAlarmVol(applicationContext, seekBar.progress)

            }
        })

        binding.seekBarVolumeRinger.max=100
        binding.seekBarVolumeRinger.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_RINGER, PrefConstants.PREF_VOLUME_RINGER_DEFAULT)
        updateTextViewPercentage(binding.textViewPercentVolumeRinger, binding.seekBarVolumeRinger.progress)
        binding.seekBarVolumeRinger?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(binding.textViewPercentVolumeRinger, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(binding.textViewPercentVolumeRinger, seekBar.progress)
                setRingerVol(applicationContext, seekBar.progress)

            }
        })

        binding.seekBarVolumeMusic.max=100
        binding.seekBarVolumeMusic.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_MUSIC, PrefConstants.PREF_VOLUME_MUSIC_DEFAULT)
        updateTextViewPercentage(binding.textViewPercentVolumeMusic, binding.seekBarVolumeMusic.progress)
        binding.seekBarVolumeMusic?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(binding.textViewPercentVolumeMusic, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(binding.textViewPercentVolumeMusic, seekBar.progress)
                setMusicVol(applicationContext, seekBar.progress)

            }
        })

        binding.seekBarVolumeNotifications.max=100
        binding.seekBarVolumeNotifications.progress=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_VOLUME_NOTIFICATION, PrefConstants.PREF_VOLUME_NOTIFICATION_DEFAULT)
        updateTextViewPercentage(binding.textViewPercentVolumeNotifications, binding.seekBarVolumeNotifications.progress)
        binding.seekBarVolumeNotifications?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateTextViewPercentage(binding.textViewPercentVolumeNotifications, seekBar.progress)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                updateTextViewPercentage(binding.textViewPercentVolumeNotifications, seekBar.progress)
                setNotificationVol(applicationContext, seekBar.progress)

            }
        })

        checkWarningVisibility()

    }

    @SuppressLint("SetTextI18n")
    fun updateTextViewPercentage(textView: TextView, percent: Int){
        textView.text= "$percent %"
        textView.setTextColor(binding.textViewLabelSliderVolumeMusic.textColors)

        if(percent<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            textView.setTextColor(Color.RED)
        }
        checkWarningVisibility()
    }

    fun checkWarningVisibility(){
        binding.warningLowVolume.visibility = View.INVISIBLE

        var warning_show=false

        if(binding.seekBarVolumeAlarm.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }
        if(binding.seekBarVolumeMusic.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }
        if(binding.seekBarVolumeNotifications.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }
        if(binding.seekBarVolumeRinger.progress<=PrefConstants.VOLUME_LOW_WARNING_THRESHOLD){
            warning_show=true
        }

        if(warning_show){
            binding.warningLowVolume.visibility = View.VISIBLE
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
