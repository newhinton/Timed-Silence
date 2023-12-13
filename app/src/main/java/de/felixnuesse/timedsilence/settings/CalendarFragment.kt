package de.felixnuesse.timedsilence.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import de.felixnuesse.timedsilence.R

class CalendarFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.calendar_preferences, rootKey)
    }
}