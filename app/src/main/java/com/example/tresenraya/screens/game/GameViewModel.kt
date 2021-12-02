package com.example.tresenraya.screens.game

import android.app.PendingIntent.getActivity
import android.graphics.Color
import android.graphics.Color.red
import android.graphics.ColorFilter
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.format.DateUtils
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.tresenraya.MainActivity
import com.example.tresenraya.R
import java.util.*

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

    private val _actualPlayer = MutableLiveData<String>()
    val actualPlayer : LiveData<String>
        get() = _actualPlayer

    private val _turn = MutableLiveData<Int>(0)
    val turn: LiveData<Int>
        get() = _turn

    private val enablePy = MutableLiveData<Boolean>(true)
    private var _symbolPlayer = MutableLiveData<String>()

    private var turnNumber = MutableLiveData<Int>(0)
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

        //_btns.value?.set(3,Color.parseColor("#FF0000"))

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
        enablePy.value = true
        playerTurn()
    }

    private fun playerTurn(){
        enablePy.value = true
    }

    private fun machineTurn(){
        enablePy.value = false


        Handler(Looper.getMainLooper()).postDelayed({
            var machinePos: Pair<Int, Int> = Pair(0, 0)

            do {
                machinePos = checkFavorableMove()
            }while( (!isPositionEmpty(machinePos)) && machinePos != null )

            //checking for gameEnded , if not , next Turn
            if (_gameEnded.value != true || _casillasVacias.value!! > 0) {

                //view.tag
                //view.setBackgroundColor(Color.GREEN)
                //view.isEnabled = false
                val id = get_casilla_id(machinePos)
                _btns.value?.set(id,Color.parseColor("#FF0000"))

                setTableroMove(machinePos)
            } else {
                _gameEnded.value = true
                onGameFinish()
            }

        },machineTurnTime) // miliseconds

    }

    private fun checkTurn(){
        _symbolPlayer.value = if(_turn.value == 0) "X" else "O"
        _actualPlayer.value = players[_turn.value!!]

        var turn = "-> Turno de ${_actualPlayer.value}"
        Log.d("Turn", turn )
    }

    private fun nextTurn() {
        if(_gameEnded.value != true){

            if(_turn.value!! >= 2) _turn.value = 0

            Log.d("Turn", "Waiting for ${_actualPlayer.value} turn to end.")
            if(_turn.value == 0){
                playerTurn()
            }else if(_turn.value == 1){
                machineTurn()
            }
            Log.d("Turn", "${_actualPlayer.value} move done. ,${_turn.value}")
        }

    }

    fun onPlayerMove(view: View){
        if(_gameEnded.value !=true){

            var id = view.tag.toString().toInt()
            Log.d("pressed", "Id:" + view.tag.toString() + " selected." )

            var coords = getTableroPosition(id)
            if(isPositionEmpty(coords)){ //isPositionEmpty()
                view.setBackgroundColor(Color.GREEN)
                view.isEnabled = false

                setTableroMove(coords)
            }else{
                Log.d("pressed", "Id:" + view.tag.toString() + " not empty.")
            }

        }

    }

    private fun setTableroMove(coords : Pair<Int,Int>){
        var x = coords.first
        var y = coords.second

        _tablero[x][y] = _symbolPlayer.value
        Log.d("move done!", "x: $x, y: $y")

        _casillasVacias.value = (_casillasVacias.value)?.minus(1)


        checkTurn()
        _turn.value = (_turn.value)?.plus(1)
        nextTurn()
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
        val pos_value = _tablero[x][y]

        Log.d("Is pos empty? pos", "$pos_value , coords: $x, $y")

        if( pos_value.isNullOrBlank() ){
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

       for (i in 1..totalCasillas){
            //var btn =  findViewById<Button>(resources.getIdentifier("button$i","id",packageName))

            //btn.setOnClickListener(::handleButtonClick)
       }

    }

    private fun restartTablero(){
        _turn.value = 0
        _gameEnded.value = false
        _casillasVacias.value = totalCasillas
        checkTurn()
    }

    private fun onGameFinish() {
        _eventGameFinished.value = true
        _gameEnded.value = true
        if(true){
            val text = "$winner WINS"
            //Toast.makeText(applicationContext, "", Toast.LENGTH_SHORT).show()
        }else{
            //Toast.makeText(applicationContext, "GAME ENDED, NO WINNER", Toast.LENGTH_SHORT).show()
        }
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


