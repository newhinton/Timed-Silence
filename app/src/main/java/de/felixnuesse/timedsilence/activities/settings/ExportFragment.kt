package de.felixnuesse.timedsilence.activities.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.felixnuesse.timedsilence.R

class ExportFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.export_preferences, rootKey)
    }
}