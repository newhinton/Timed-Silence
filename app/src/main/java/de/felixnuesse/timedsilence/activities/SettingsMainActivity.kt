package de.felixnuesse.timedsilence.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import de.felixnuesse.timedintenttrigger.database.xml.Exporter
import de.felixnuesse.timedintenttrigger.database.xml.Importer
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_LOUD
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_SILENT
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_UNSET
import de.felixnuesse.timedsilence.Constants.Companion.TIME_SETTING_VIBRATE
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.ThemeHandler
import kotlinx.android.synthetic.main.activity_settings_main.*
import android.widget.AdapterView

import android.widget.AdapterView.OnItemSelectedListener
import de.felixnuesse.timedsilence.PrefConstants.Companion.TIME_SETTING_DEFAULT
import de.felixnuesse.timedsilence.PrefConstants.Companion.TIME_SETTING_DEFAULT_PREFERENCE


class SettingsMainActivity : AppCompatActivity() {

    private val mDefaultVolumeSettings = ArrayList<String>()
    private val mDefaultVolumeSettingIDs = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeHandler.setTheme(this, window)
        ThemeHandler.setSupportActionBarTheme(this, supportActionBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_settings_main)

        //sets the actionbartitle
        title = resources.getString(R.string.actionbar_title_settings)

        mDefaultVolumeSettingIDs.add(TIME_SETTING_SILENT)
        mDefaultVolumeSettingIDs.add(TIME_SETTING_VIBRATE)
        mDefaultVolumeSettingIDs.add(TIME_SETTING_LOUD)
        mDefaultVolumeSettingIDs.add(TIME_SETTING_UNSET)

        mDefaultVolumeSettings.add(resources.getString(R.string.volume_setting_silent))
        mDefaultVolumeSettings.add(resources.getString(R.string.volume_setting_vibrate))
        mDefaultVolumeSettings.add(resources.getString(R.string.volume_setting_loud))
        mDefaultVolumeSettings.add(resources.getString(R.string.volume_setting_unset))


        volumeSettingsLayout.setOnClickListener {
            Log.e(Constants.APP_NAME, "SettingsMain: Click volume settings")
            openVolumeSettings()
        }

        volumeSettingsImageButton.setOnClickListener {
            Log.e(Constants.APP_NAME, "SettingsMain: Click volume settings")
            openVolumeSettings()
        }


        switchHeadsetIgnoreChange.isChecked=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET_DEFAULT)
        switchHeadsetIgnoreChange.setOnCheckedChangeListener { _, checked ->
            writeHeadsetSwitchSetting(this, checked)
        }

        switchIgnoreAllDay.isChecked=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_IGNORE_ALL_DAY_EVENTS, PrefConstants.PREF_IGNORE_ALL_DAY_EVENTS_DEFAULT)
        switchIgnoreAllDay.setOnCheckedChangeListener { _, checked ->
            writeIgnoreAlldaySwitchSetting(this, checked)
        }

        export_button.setOnClickListener {
            Log.e(Constants.APP_NAME, "SettingsMain: Click export")
            Exporter.export(this)
        }

        import_button.setOnClickListener {
            Log.e(Constants.APP_NAME, "SettingsMain: Click import")
            Importer.importFile(this)
        }

        setSwitches()
        switchThemeDark.setOnCheckedChangeListener { _, checked ->
            if(checked) applyTheme(PrefConstants.PREF_DARKMODE_DARK)
        }

        switchThemeLight.setOnCheckedChangeListener { _, checked ->
            if(checked) applyTheme(PrefConstants.PREF_DARKMODE_LIGHT)
        }

        switchThemeAuto.setOnCheckedChangeListener { _, checked ->
            if(checked) applyTheme(PrefConstants.PREF_DARKMODE_AUTO)
        }

        switchPauseNotification.isChecked = SharedPreferencesHandler.getPref(this, PrefConstants.PREF_PAUSE_NOTIFICATION, PrefConstants.PREF_PAUSE_NOTIFICATION_DEFAULT)
        switchPauseNotification.setOnCheckedChangeListener { _, checked ->
            writePauseNotificationSwitchSetting(this, checked)
        }

        switchAllowTriggerWhileIdle.isChecked = SharedPreferencesHandler.getPref(this, PrefConstants.PREF_RUN_ALARMTRIGGER_WHEN_IDLE, PrefConstants.PREF_RUN_ALARMTRIGGER_WHEN_IDLE_DEFAULT)
        switchAllowTriggerWhileIdle.setOnCheckedChangeListener { _, checked ->
            val sharedPreferences: SharedPreferences.Editor? =
                PreferenceManager.getDefaultSharedPreferences(this).edit()
            sharedPreferences?.putBoolean(
                PrefConstants.PREF_RUN_ALARMTRIGGER_WHEN_IDLE,
                checked
            )
        }

        val spinner: Spinner = findViewById(R.id.spinner_defaultVolume)
        // Create an ArrayAdapter using the string array and a default spinner layout
        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            R.layout.spinner_default_volume, R.id.textview_spinner,
            mDefaultVolumeSettings
        )
        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                SharedPreferencesHandler.setPref(baseContext, TIME_SETTING_DEFAULT_PREFERENCE, mDefaultVolumeSettingIDs[position])
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                SharedPreferencesHandler.setPref(baseContext, TIME_SETTING_DEFAULT_PREFERENCE, TIME_SETTING_DEFAULT)
            }
        }

        val selectedDefault = mDefaultVolumeSettingIDs.indexOf(SharedPreferencesHandler.getPref(baseContext, TIME_SETTING_DEFAULT_PREFERENCE, TIME_SETTING_DEFAULT))
        spinner.setSelection(selectedDefault)

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

    private fun openVolumeSettings() {
        val intent = Intent(this, SettingsVolumeActivity::class.java).apply {}
        startActivity(intent)
    }

    private fun writeHeadsetSwitchSetting(context: Context, value: Boolean) {
       SharedPreferencesHandler.setPref(context, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, value)
    }

    private fun writeIgnoreAlldaySwitchSetting(context: Context, value: Boolean) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_IGNORE_ALL_DAY_EVENTS, value)
    }

    private fun writeThemeSwitchSetting(context: Context, value: Int) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_DARKMODE, value)
    }

    private fun writePauseNotificationSwitchSetting(context: Context, value: Boolean) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_PAUSE_NOTIFICATION, value)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        Exporter.onRequestPermissionsResult(this, requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Importer.onActivityResult(this, requestCode, resultCode, data)
    }

    fun applyTheme(mode: Int){
        writeThemeSwitchSetting(this, mode)
        ThemeHandler.setTheme(this, window)
        ThemeHandler.setSupportActionBarTheme(this, supportActionBar)
        setSwitches()
    }

    fun setSwitches(){
        switchThemeDark.isChecked=false
        switchThemeLight.isChecked=false
        switchThemeAuto.isChecked=false
        switchThemeDark.isClickable = true;
        switchThemeLight.isClickable=true
        switchThemeAuto.isClickable=true

        when(SharedPreferencesHandler.getPref(this, PrefConstants.PREF_DARKMODE, PrefConstants.PREF_DARKMODE_DEFAULT)) {
            PrefConstants.PREF_DARKMODE_DARK -> {switchThemeDark.isChecked=true;switchThemeDark.isClickable=false}
            PrefConstants.PREF_DARKMODE_LIGHT -> {switchThemeLight.isChecked=true;switchThemeLight.isClickable=false}
            PrefConstants.PREF_DARKMODE_AUTO-> {switchThemeAuto.isChecked=true;switchThemeAuto.isClickable=false}
            else -> switchThemeLight.isChecked=true
        }
    }
}
