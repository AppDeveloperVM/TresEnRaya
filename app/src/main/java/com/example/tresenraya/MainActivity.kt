package com.example.tresenraya

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import java.util.*
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {


        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


    }

    override fun onResume() {
        super.onResume()

        if(getSharedPreferences("cat.copernic.vicm.tresenrayaa_preferences", MODE_PRIVATE).getBoolean("darkmode", false)){
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
    }


}