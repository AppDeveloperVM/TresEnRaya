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
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val filas = 3
    private val columnas = 3
    private val totalCasillas : Int = filas * columnas
    private var casillasVacias : Int = totalCasillas
    //private var tablero = mutableMapOf<Int,String>()
    private var _tablero= Array(filas) { arrayOfNulls<String>(columnas) } //Array(filas) { Array<String>(size = columnas) }

    private val players = arrayOf("Jugador", "Máquina")
    private lateinit var turntxt: TextView // TURNO
    private var turnNumber: Int = 0
    private var actual_player: String = ""
    private lateinit var symbol_player: String

    private lateinit var reset: Button

    private var mc_turn_end : Boolean = false
    private var enable_py: Boolean = true
    private val machine_turn_time : Long = 400

    private var game_ended: Boolean = false
    private var winner: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }


}