package de.felixnuesse.timedsilence.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.felixnuesse.timedsilence.R

class GeneralFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey)
    }
}