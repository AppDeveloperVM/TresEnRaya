package com.example.vmareeredondotresenraya.screens.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.app.ActivityCompat.recreate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.vmareeredondotresenraya.MainActivity
import com.example.vmareeredondotresenraya.R
import android.content.Intent




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

        //Log.d(TAG, "${preference?.key}")
        return super.onPreferenceTreeClick(preference)
    }


    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        preferenceManager.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onPause()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {

        if(key == "dark_mode"){
            val value = sharedPreferences!!.getBoolean(key, false)
            if(value){
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
            }else if(!value){
                AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
            }



            //AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        }
    }


}