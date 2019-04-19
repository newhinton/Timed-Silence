package de.felixnuesse.timedsilence.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import kotlinx.android.synthetic.main.activity_settings_main.*

class SettingsMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_main)

        volumeSettingsLayout.setOnClickListener {
            Log.e(Constants.APP_NAME, "SettingsMain: Click volume settings")
            openVolumeSettings()
        }


        switchHeadsetIgnoreChange.isChecked=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT)
        switchHeadsetIgnoreChange.setOnCheckedChangeListener { _, checked ->
                writeSwitchSetting(this, checked)
        }
    }



    fun openVolumeSettings() {
        val intent = Intent(this, SettingsVolumeActivity::class.java).apply {}
        startActivity(intent)
    }

    fun writeSwitchSetting(context: Context, value: Boolean) {
       SharedPreferencesHandler.setPref(context, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, value)
    }

}
