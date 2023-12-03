package de.felixnuesse.timedsilence.activities.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.felixnuesse.timedsilence.activities.SettingsActivity
import de.felixnuesse.timedsilence.databinding.FragmentSettingsSelectorBinding

class SelectorFragment(private var mParent: SettingsActivity) : Fragment() {

    private var _binding: FragmentSettingsSelectorBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.volumeSettings.setOnClickListener {
            mParent.openFragment(VolumeFragment())
        }

        binding.calendarSettings.setOnClickListener {
            mParent.openFragment(CalendarFragment())
        }

        binding.generalSettings.setOnClickListener {
            mParent.openFragment(GeneralFragment())
        }

        binding.exportSettings.setOnClickListener {
            mParent.openFragment(GeneralFragment())
        }

        binding.importSettings.setOnClickListener {
            mParent.openFragment(GeneralFragment())
        }
    }
}