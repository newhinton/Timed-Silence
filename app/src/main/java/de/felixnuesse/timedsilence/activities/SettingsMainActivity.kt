package de.felixnuesse.timedsilence.activities

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.NavUtils
import de.felixnuesse.timedintenttrigger.database.xml.Exporter
import de.felixnuesse.timedintenttrigger.database.xml.Importer
import de.felixnuesse.timedsilence.Constants
import de.felixnuesse.timedsilence.PrefConstants
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.SharedPreferencesHandler
import de.felixnuesse.timedsilence.handler.ThemeHandler
import kotlinx.android.synthetic.main.activity_settings_main.*



class SettingsMainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ThemeHandler.setTheme(this, window)
        ThemeHandler.setSupportActionBarTheme(this, supportActionBar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_settings_main)



        //sets the actionbartitle
        title = resources.getString(R.string.actionbar_title_settings)


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

        switchPauseNotification.isChecked=SharedPreferencesHandler.getPref(this, PrefConstants.PREF_PAUSE_NOTIFICATION, PrefConstants.PREF_PAUSE_NOTIFICATION_DEFAULT)
        switchPauseNotification.setOnCheckedChangeListener { _, checked ->
            writePauseNotificationSwitchSetting(this, checked)
        }
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

    fun openVolumeSettings() {
        val intent = Intent(this, SettingsVolumeActivity::class.java).apply {}
        startActivity(intent)
    }

    fun writeHeadsetSwitchSetting(context: Context, value: Boolean) {
       SharedPreferencesHandler.setPref(context, PrefConstants.PREF_IGNORE_CHECK_WHEN_HEADSET, value)
    }

    fun writeThemeSwitchSetting(context: Context, value: Int) {
        SharedPreferencesHandler.setPref(context, PrefConstants.PREF_DARKMODE, value)
    }

    fun writePauseNotificationSwitchSetting(context: Context, value: Boolean) {
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
