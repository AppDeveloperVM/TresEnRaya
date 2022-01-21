package com.example.vmareeredondotresenraya

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.preference.PreferenceManager


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val darkmode = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("darkmode",false)

        if(!darkmode){

            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
        }

    }


}