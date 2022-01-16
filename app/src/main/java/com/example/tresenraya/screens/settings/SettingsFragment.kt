package com.example.tresenraya.screens.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.tresenraya.R

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener  {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

       /* val preference = findPreference(
            getString(R.string.title_text) // Change this to the preference key set in the settings XML file

        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this@SettingsFragment.requireContext())
        */
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext())

        Log.d("INFO", "${preference?.key}")
        return super.onPreferenceTreeClick(preference)
    }


    override fun onResume() {
        Log.i("HELP",preferenceManager.sharedPreferencesName);
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        if(key == "darkmode"){
            when (sharedPreferences?.getBoolean(key, false)){
                true -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
                false -> AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
            }
        }
        Log.d("MODE", "${sharedPreferences?.getBoolean(key, false)}")
    }


}