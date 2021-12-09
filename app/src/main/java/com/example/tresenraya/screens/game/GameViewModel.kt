package com.example.tresenraya.screens.game

import android.graphics.Color
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.tresenraya.R
import java.util.*
import android.widget.TextView
import androidx.databinding.adapters.Converters


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

    private var winner: String = ""

    /*
    private val _tablero: MutableLiveData<List<String>> by lazy {
        MutableLiveData<List<String>>().apply {
            value = emptyList()
        }
    }

    val tablero : LiveData<List<String>>
        get() = _tablero
     */

    private var _tablero = Array(filas) { arrayOfNulls<String>(columnas) }
    private val players = arrayOf("Jugador","Maquina")
    private val _btns = MutableLiveData(Array(9) { Color.parseColor("#b2a8ba") })
    val btns : LiveData<Array<Int>>
        get() = _btns

    private val _btn1 = MutableLiveData<Int>( )
    val btn1 : LiveData<Int>
        get() = _btn1

    private val _pyEnabled = MutableLiveData<Boolean>()
    val pyEnabled : LiveData<Boolean>
        get() = _pyEnabled

    private val _actualPlayer = MutableLiveData<String>()
    val actualPlayer : LiveData<String>
        get() = _actualPlayer

    private val _turn = MutableLiveData<Int>(0)
    val turn: LiveData<Int>
        get() = _turn

    private var _symbolPlayer = MutableLiveData<String>()

    private val machineTurnTime: Long = 1000



    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int>
        get() = _score



    private val _eventGameFinished = MutableLiveData<Boolean>(false)
    val eventGameFinished: LiveData<Boolean>
        get() = _eventGameFinished

    private val _currentTime = MutableLiveData<Long>()
    private val currentTime: LiveData<Long>
        get() = _currentTime

    private val timer: CountDownTimer

    val currentTimeString: LiveData<String> = Transformations.map(currentTime) { time ->
        DateUtils.formatElapsedTime(time)
    }


    init {

        timer = object : CountDownTimer(COUNTDOWN_TIME, ONE_SECOND){

            override fun onTick(millisUntilFinished: Long) {
                _currentTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _currentTime.value = DONE
            }
        }

        startGame()
        timer.start()
    }


    private fun startGame(){
        initTablero()
        restartTablero()
        turnoInicial()
    }

    private fun turnoInicial(){
        _turn.value = 0
        _pyEnabled.value = true
        playerTurn()
    }

    private fun playerTurn(){
        _pyEnabled.value = true
    }

    private fun machineTurn(){
        _pyEnabled.value = false

        Handler(Looper.getMainLooper()).postDelayed({
            var machinePos: Pair<Int, Int> = Pair(0, 0)

            do {
                machinePos = checkFavorableMove()
            }while( !isPositionEmpty(machinePos) )


            //checking for gameEnded , if not , next Turn
            if (_gameEnded.value != true || _casillasVacias.value!! > 0) {
                val id = get_casilla_id(machinePos)
                Log.d("casilla: ", "${_btns.value?.get(id)}" )

                _btns.value?.set(2,Color.BLACK)// cambio a color ROJO
                Log.d("Turno, button changed", "${_btns.value?.get(id) }" )


                setTableroMove(machinePos)
            } else {
                _gameEnded.value = true
                onGameFinish()
            }

        },machineTurnTime) // miliseconds



    }



    private fun checkTurn(){
        if(_turn.value!! >= 2) _turn.value = 0
        _symbolPlayer.value = if(_turn.value == 0) "X" else "O"
        _actualPlayer.value = players[_turn.value!!]

    }

    private fun nextTurn() {
        if(_gameEnded.value != true && _casillasVacias.value!! > 0){


            if(_turn.value == 0){
                playerTurn()
            }else if(_turn.value == 1){
                machineTurn()
            }

        }else{
            onGameFinish()
        }

    }

    fun onPlayerMove(view: View){
        if(_gameEnded.value !=true && _pyEnabled.value == true){

            var id = view.tag.toString().toInt()
            Log.d("pressed", "Id:" + view.tag.toString() + " selected." )

            var coords = getTableroPosition(id)
            if(isPositionEmpty(coords)){ //isPositionEmpty
                view.setBackgroundColor(Color.GREEN)
                view.isEnabled = false

                setTableroMove(coords)
            }else{
                Log.d("pressed", "Id:" + view.tag.toString() + " not empty.")
            }

        }

    }

    private fun setTableroMove(coords : Pair<Int,Int>){
        if(_gameEnded.value == false) {
            var x = coords.first
            var y = coords.second

            _tablero[x][y] = _symbolPlayer.value
            Log.d("Turn", "Mode DONE! casilla -> x: $x, y: $y")
            Log.d("Turn", "Turno de ${_actualPlayer.value} finalizado")

            if( checkWinnerPlay( coords, _symbolPlayer.value.toString() ) ){
                onGameFinish()
            }else{
                _turn.value = (_turn.value)?.plus(1)
                _casillasVacias.value = (_casillasVacias.value)?.minus(1)
                checkTurn()

                Log.d("Turn", "-------------------------------")
                Log.d("Turn", "Turno de ${_actualPlayer.value}")
                Log.d("Turn", "Waiting for ${_actualPlayer.value} turn to end.")

                nextTurn()
            }



        }
    }

    private fun checkFavorableMove() : Pair<Int, Int>{
        var coords : Pair<Int,Int> = Pair(0,0)

        // H ( 0,1,2,  3,4,5,  6,7,8 ) +1
        // V ( 0,3,6,  1,4,7,  2,5,8 ) +3
        // D ( 0,4,8,  2,4,6 ) +2
        var symbols = arrayOf("O","X")
        var combinations = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

         //combinations for attack and defend
        for(player in symbols){

            for(coords in combinations){
            // 3 positions from combination
            //return posicion favorable
    
                //primero y segundo
                if( getCoordsValue(getTableroPosition(coords[0])) == getCoordsValue(getTableroPosition(coords[1])) &&
                getCoordsValue(getTableroPosition(coords[0])) == player /*symbol_player*/
                && getCoordsValue(getTableroPosition(coords[2])) == null
                ){
                //return tercera posicion
                return getTableroPosition(coords[2])
    
                //primero y tercero
                }else if(
                getCoordsValue(getTableroPosition(coords[0])) == getCoordsValue(getTableroPosition(coords[2])) &&
                getCoordsValue(getTableroPosition(coords[0])) == player
                && getCoordsValue(getTableroPosition(coords[1])) == null
                ){
                //return segunda posicion
                return getTableroPosition(coords[1])
    
                //segundo y tercero
                }else if(getCoordsValue(getTableroPosition(coords[1])) == getCoordsValue(getTableroPosition(coords[2])) &&
                getCoordsValue(getTableroPosition(coords[1])) == player
                && getCoordsValue(getTableroPosition(coords[0])) == null
                ){
                    //return primera posicion
                    return getTableroPosition(coords[0])
                }
    
            }

        }

        //Do random position
        if(coords.first == 0 && coords.second == 0){
            do {
                var pos = Random().nextInt(9) // random number
                coords = getTableroPosition(pos)
                val empty = isPositionEmpty( coords )
            }while (! empty && coords!= null)
        }


        return coords
    }

    private fun checkWinnerPlay(posicion : Pair<Int,Int>, symbol : String): Boolean{
        var row = posicion.first
        var column = posicion.second

        if( areEqual(_tablero[row][0], _tablero[row][1], _tablero[row][2], symbol)//check horizontals
        ||
        areEqual( _tablero[0][column], _tablero[1][column], _tablero[2][column],symbol )//check verticals
        ||
        areEqual( _tablero[0][0], _tablero[1][1], _tablero[2][2], symbol  )//check diagonals
        ||
        areEqual( _tablero[0][2], _tablero[1][1], _tablero[2][0], symbol  )
        ){
        winner = players[_turn.value!!]
        Log.d("turn", "WINNER : $winner")

            return true
        }

        return false
    }

    private fun getCoordsValue(position : Pair<Int,Int>) : String? {
        val x = position.first
        val y = position.second
        var pos_value = _tablero[x][y]

        if(! pos_value.isNullOrBlank() ){
            return pos_value
        }
        
        return null
    }

    private fun getTableroPosition(casilla: Int) : Pair<Int, Int>{
        var cont:Int = 0

        for(index_a in 0 until filas){
            for(index_b in 0 until columnas){

            if(cont == casilla){
                //Log.d("POSITION", "$index_a,$index_b")
                return Pair(index_a,index_b)
            }
                cont++
            }
        }
        return Pair(0,0)
    }

    private fun get_casilla_id(posicion : Pair<Int, Int>) : Int{
        var cont:Int = 0

        for(index_a in 0 until filas){
            for(index_b in 0 until columnas){

                if(posicion.first == index_a && posicion.second == index_b){
                    return cont
                }
            cont++
            }
        }
        return cont
    }

    private fun isPositionEmpty(position : Pair<Int,Int>): Boolean{
        val x = position.first
        val y = position.second
        val posValue = _tablero[x][y]

        Log.d("Is casilla empty? pos", "$posValue , coords: $x, $y")

        if( posValue.isNullOrBlank() ){
        return true
        }

        return false
    }

    private fun areEqual(a:String?,b:String?,c:String?, symbol: String): Boolean{

        if( symbol == a && a == b && b == c){
            return true
        }
            return false
    }

    private fun initTablero(){

       for (i in 0 until totalCasillas){
           // set color = grey(default)
           _btns.value?.set(i,Color.parseColor("#b2a8ba"))
       }

    }

    private fun restartTablero(){
        _turn.value = 0
        _gameEnded.value = false
        _tablero = Array(filas) { arrayOfNulls<String>(columnas) }
        _casillasVacias.value = totalCasillas
        checkTurn()
    }

    private fun onGameFinish() {
        _eventGameFinished.value = true
        _gameEnded.value = true
    }

    fun onNewGame(){
        initTablero()
        restartTablero()
        turnoInicial()
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


