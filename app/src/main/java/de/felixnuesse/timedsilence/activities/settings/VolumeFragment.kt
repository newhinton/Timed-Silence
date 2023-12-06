package de.felixnuesse.timedsilence.activities.settings

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import de.felixnuesse.timedsilence.R


class VolumeFragment : PreferenceFragmentCompat() {



    private val minVol = 30
    private var defaultVol = 100

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.volume_preferences, rootKey)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        defaultVol = requireContext().resources.getInteger(R.integer.pref_volume_default)

        attachWarning(requireContext().getString(R.string.pref_volume_notification), "ALERT_notification")
        attachWarning(requireContext().getString(R.string.pref_volume_alarm), "ALERT_alarm")
        attachWarning(requireContext().getString(R.string.pref_volume_ringer), "ALERT_ringer")
    }

    private fun attachWarning(sliderID: String, alertID: String) {
        val warning = findPreference(alertID) as Preference?
        val slider: SeekBarPreference? = findPreference(sliderID)

        slider?.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                if(Integer.valueOf(newValue.toString()) < minVol) {
                    warning?.isVisible = true
                    true
                } else {
                    warning?.isVisible = false
                    true
                }
            }

        warning?.isVisible = (slider?.value?: defaultVol) < minVol
    }

}
