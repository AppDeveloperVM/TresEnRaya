package com.example.tresenraya.screens.game

import android.os.CountDownTimer
import android.text.format.DateUtils
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.NavHostFragment

class GameViewModel : ViewModel() {

    private val filas: Int = 3
    private val columnas: Int = 3
    private val totalCasillas : Int = filas * columnas
    private val _casillasVacias = MutableLiveData<Int>(totalCasillas)
    val casillasVacias : LiveData<Int>
        get() = _casillasVacias


    private val _gameEnded = MutableLiveData<Boolean>(false)
    val gameEnded: LiveData<Boolean>
            get() = _gameEnded


    private val winner: String = ""

    private val _tablero: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>().apply {
            value = emptyList()
        }
    }

    val tablero : LiveData<List<String>>
        get() = _tablero


    private val _players = MutableLiveData<Array<String>>(arrayOf("Jugador","Maquina"))
    val players : LiveData<Array<String>>
        get() = _players

    private val _actualPlayer = MutableLiveData<String>()
    val actualPlayer : LiveData<String>
        get() = _actualPlayer

    private val _turn = MutableLiveData<Int>(0)
    val turn: LiveData<Int>
        get() = _turn

    private val enablePy = MutableLiveData<Boolean>(true)
    private var _symbolPlayer = MutableLiveData<String>()
    val symbolPlayer : LiveData<String>
        get() = _symbolPlayer

    private var turnNumber = MutableLiveData<Int>(0)
    //private lateinit var turnText: TextView



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

        //turnText = findViewById(R.id.turnText)

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



    private fun turnoInicial(){
        turnNumber.value = 0
        enablePy.value = true
        playerTurn()
    }

    private fun checkTurn(){
        //_actualPlayer.value = players
        _symbolPlayer.value = if(turnNumber.value == 0) "X" else "O"
        var turn = "Turno de $actualPlayer"
        //turntxt.text = turn
    }

    private fun cambioTurno(){

        if(_gameEnded.value != true){
            if(turnNumber.value == 1){
                machineTurn()
            }else{
                playerTurn()
            }
        }
    }

    private fun playerTurn(){
        enablePy.value = true
    }

    private fun machineTurn(){
        enablePy.value = true
    }

    fun onTurnMove(){

        _casillasVacias.value?.minus(1)
    }

    private fun nextTurn() {
        _symbolPlayer.value = if(turnNumber.value == 0) "X" else "O"
    }


    private fun restartTablero(){
        turnNumber.value = 0
        _gameEnded.value = false
        _casillasVacias.value = totalCasillas
        casillasVacias.value
        checkTurn()
    }


    fun onNewGame(){
        restartTablero()
        turnoInicial()
    }

    fun onGameFinish() {
        _eventGameFinished.value = true
        _gameEnded.value = true
        if(true){
            val text = "$winner WINS"
            //Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
        }else{
            //Toast.makeText(applicationContext, "GAME ENDED, NO WINNER", Toast.LENGTH_SHORT).show()
        }
    }

    fun onGameFinishComplete() {
        _eventGameFinished.value = false
    }


    companion object {

        private const val DONE = 0L

        private const val ONE_SECOND = 1_000L

        private const val COUNTDOWN_TIME = 60_000L

    }

}
