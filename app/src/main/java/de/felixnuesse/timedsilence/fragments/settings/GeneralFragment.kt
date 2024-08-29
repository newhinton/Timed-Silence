package de.felixnuesse.timedsilence.fragments.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import de.felixnuesse.timedsilence.R

class GeneralFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences!!.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences!!.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val pref_key = getString(R.string.pref_general_search_notifications)
        if (key.equals(pref_key)) {
           if (sharedPreferences?.getBoolean(pref_key, false) == true) {
               ContextCompat.startActivity(this.requireContext(), Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS), null)
           }
        }
    }
}