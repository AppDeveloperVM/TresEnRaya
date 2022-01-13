package com.example.tresenraya.screens.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.tresenraya.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

       /* val preference = findPreference(
            getString(R.string.title_text) // Change this to the preference key set in the settings XML file

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this@SettingsFragment.requireContext())
        */
    }

    override fun onResume() {
        super.onResume()
        preferenceManager
            .sharedPreferences
            //.registerOnSharedPreferenceChangeListener(this)
    }


}