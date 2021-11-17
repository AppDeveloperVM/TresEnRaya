package com.example.tresenraya.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private val filas = 3
    private val columnas = 3
    /*private var _tablero = MutableLiveData<Array<String?>>(3,"")
    var tablero: LiveData<Array<String?>>
        get() = _tablero

     */

    private val _turn = MutableLiveData<Int>(0)
    val turn: LiveData<Int>
        get() = _turn

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score

    private val _eventGameFinished = MutableLiveData<Boolean>(false)
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    private val _currentTime = MutableLiveData<Long>()
    val currentTime: LiveData<Long>
        get() = _currentTime

    private val timer: CountDownTimer

    val currentTimeString: LiveData<String> = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }





    init {
        //resetList()
        //nextTurn()

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _currentTime.value = DONE
            }
        }

        timer.start()
    }


    private fun nextTurn() {
        symbolPlayer = if(turnNumber == 0) "X" else "O"
    }


    fun onGameFinish() {
        _eventGameFinished.value = true
    }

    fun onGameFinishComplete() {
        _eventGameFinished.value = false
    }


    companion object {
        private const val DONE = 0L

        private const val ONE_SECOND = 1_000L

        private const val COUNTDOWN_TIME = 60_000L

        private val players = arrayOf("Jugador", "Máquina")

        private var symbolPlayer : String = ""

        private var turnNumber : Int = 0




    }

}
