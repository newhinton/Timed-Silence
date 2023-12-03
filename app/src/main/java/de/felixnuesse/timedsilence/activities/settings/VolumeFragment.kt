package de.felixnuesse.timedsilence.activities.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.felixnuesse.timedsilence.R
import de.felixnuesse.timedsilence.handler.PreferencesManager

class VolumeFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.volume_preferences, rootKey)
    }
}