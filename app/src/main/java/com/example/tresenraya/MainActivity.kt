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


class MainActivity : AppCompatActivity() {

    private val filas = 3
    private val columnas = 3
    private val totalCasillas : Int = filas * columnas
    private var casillasVacias : Int = totalCasillas
    //private var tablero = mutableMapOf<Int,String>()
    private var _tablero= Array(filas) { arrayOfNulls<String>(columnas) } //Array(filas) { Array<String>(size = columnas) }

    private val players = arrayOf("Jugador", "Máquina")
    private lateinit var turntxt: TextView // TURNO
    private var turn_number: Int = 0
    private var actual_player: String = "";

    private var buttons: List<Button> = ArrayList()
    private lateinit var reset: Button

    private var mc_turn_end : Boolean = false
    private var enable_py: Boolean = true

    private var game_ended: Boolean = false
    private var winner: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        turntxt = findViewById(R.id.turntxt)
        reset = findViewById(R.id.reset)

        start_game()
    }

    fun start_game(){
        init_tablero()
        reset_tablero()
        turno_inicial()


        /*var pair : Pair<Int,Int> = Pair(2,2)
        var res = get_casilla_id(pair)
        Toast.makeText(applicationContext, "Id of pos 2,2 :$res", Toast.LENGTH_SHORT).show()*/
    }

    //--------------TURNOS-----------------
    fun turno_inicial() {
        turn_number = 0;
        check_turno()
        enable_py = true
        player_turn()
    }

    fun player_turn(){
        Log.d("Waiting", " for player turn to end.")
        mc_turn_end = false
        enable_py = true
    }

    fun machine_turn(){
        //check_turno()
        enable_py = false

        Log.d("Waiting", " for machine turn to end.")
        //changeInputPermission()

        Handler(Looper.getMainLooper()).postDelayed({
            var machine_pos: Int

            do {


                do{
                    machine_pos = Random().nextInt(9) // random number
                //}while (tablero.containsKey(randomNumber)) //not equal to a number already set in tablero
                }while ( !is_position_empty(machine_pos) )

                val coords = get_tablero_position(machine_pos)
                val x = coords.first
                val y = coords.second

            } while (
                checkJugadaFavorable(Pair(x, y)) != Pair(x, y)
            )

            buttonSelected(machine_pos)
            Log.d("End", " machine turn ended.")
            mc_turn_end = true

            //if(_tablero.size < 9) {
            if(casillasVacias > 0) {
                cambio_turno()
            }

        }, 1000) // seconds

        //changeInputPermission()
    }

    fun check_turno(){
        if(casillasVacias == 0){
            end_game()
        }

        actual_player = players[turn_number] //
        Log.d("Turno", "$actual_player")
        turntxt.text = actual_player
    }

    fun cambio_turno() {
        if(!game_ended) {
            if (turn_number == 1) {
                machine_turn()
            } else {
                player_turn()
            }
        }

    }
    //-------------------------------------

    fun buttonSelected(id : Int){
        lateinit var btn_selected : Button
        when(id){
            0->btn_selected = findViewById(R.id.button1)
            1->btn_selected = findViewById(R.id.button2)
            2->btn_selected = findViewById(R.id.button3)
            3->btn_selected = findViewById(R.id.button4)
            4->btn_selected = findViewById(R.id.button5)
            5->btn_selected = findViewById(R.id.button6)
            6->btn_selected = findViewById(R.id.button7)
            7->btn_selected = findViewById(R.id.button8)
            8->btn_selected = findViewById(R.id.button9)
        }

        hacer_jugada(btn_selected)
        /*mc_turn_end = true
        check_turno()*/
    }

    fun handleButtonClick(view: View) {
        if(enable_py && !game_ended){
            hacer_jugada(view as Button)
            Log.d("End", " player turn ended.")
            //mc_turn_end = false
            //if(tablero.size < 9) {
            if(casillasVacias > 0){
                cambio_turno()
            }
        }
    }

    fun hacer_jugada(btn: Button){
        //var id_b = getResources().getIdentifier("button"+ , "id", getPackageName());
        var btn_id = btn.getTag().toString()
        var id:Int = Integer.parseInt(btn_id)
        Log.d("Id", "$btn_id")

        //id de celda
        //x per player, o per machine
        var symbol_player: String
        symbol_player = if(turn_number == 0) "X" else "O"

        //tablero.put(id,symbol_player)
        //Log.d("added", "Id:"+id+" -> "+tablero[id].toString())

        var posicion_x_y = get_tablero_position(id);
        var x = posicion_x_y.first
        var y = posicion_x_y.second

        if(turn_number == 1) {
            val favorable_pos = checkJugadaFavorable(Pair(x, y))
            if (favorable_pos.first != x && favorable_pos.second != y) {
                x = favorable_pos.first
                y = favorable_pos.second
            }
        }

        _tablero[x][y] = symbol_player
        Log.d("added", "Id:"+id+" -> pos: "+x+","+y )

        if ( checkWinnerPlay(get_tablero_position(id), symbol_player) ){
            winner = players[turn_number]
            end_game()
        }

        //btn.setEnabled(false)
        if(turn_number == 0){
            btn.setBackgroundColor(Color.GREEN)
        }else if(turn_number == 1){
            btn.setBackgroundColor(Color.RED)
        }
        btn.setEnabled(false)

        turn_number = (turn_number + 1) % 2
        casillasVacias--

        check_turno()
    }

    fun is_position_empty(id:Int): Boolean{
        val position = get_tablero_position(id)

        val x = position.first
        val y = position.second

        val pos_value = _tablero[x][y]
        Log.d("Is pos empty? pos", "$id: $pos_value , coords: $x, $y")

        if( pos_value.isNullOrBlank() ){
            return true
        }

        return false
    }

    fun findIndex(arr: Array<Int>, item: Int): Int {
        return arr.indexOf(item)
    }

    //disable/enable inputs
    fun changeInputPermission(){
        var access: Boolean = false
        if(turn_number == 0){
            access = true
        }else if(turn_number == 1){
            access = false
        }

        for (i in 1..totalCasillas){
            var btn = findViewById<Button>(resources.getIdentifier("button$i","id",packageName))
            btn.setEnabled(access)
            if(access){
                btn.setOnClickListener(::handleButtonClick)
            }
        }
    }

    fun init_tablero(){
        for (i in 1..totalCasillas){
            var btn = findViewById<Button>(resources.getIdentifier("button$i","id",packageName))
            btn.setOnClickListener(::handleButtonClick)
            btn.setTag(i - 1)
        }
        reset.setOnClickListener{
            reset_tablero()
        }
    }

    fun end_game(){
        game_ended = true
        if(winner != ""){
            Toast.makeText(applicationContext, "GAME ENDED, winner is "+ winner, Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(applicationContext, "GAME ENDED, NO WINNER", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkWinner(): String{
        winner = players[turn_number]
        return winner
    }

    fun get_tablero_position(casilla: Int) : Pair<Int, Int>{
        //primera casilla, ultima fila [2][0]

        //var nuevo_array = Pair()
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

    fun get_casilla_id(posicion : Pair<Int, Int>) : Int{
        //primera casilla, ultima fila [2][0]

        //var nuevo_array = Pair()
        var cont:Int = 0

        for(index_a in 0 until filas){
            for(index_b in 0 until columnas){

                if(posicion.first == index_a && posicion.second == index_b){
                    //Log.d("POSITION", "$index_a,$index_b")
                    return cont
                }
                cont++
            }
        }
        return cont
    }

    fun checkWinnerPlay(posicion : Pair<Int,Int>, symbol : String): Boolean{
        var row = posicion.first
        var column = posicion.second

        //check horizontals
        if( areEqual(_tablero[row][0], _tablero[row][1], _tablero[row][2], symbol) ) {
            //Toast.makeText(applicationContext, "horizontal winner,"+row+","+column, Toast.LENGTH_SHORT).show()
            return true
        }
        //check verticals
        if( areEqual( _tablero[0][column], _tablero[1][column], _tablero[2][column],symbol ) ){
            //Toast.makeText(applicationContext, "vertical winner,"+row+","+column, Toast.LENGTH_SHORT).show()
            return true
        }

        //check diagonals
        if( areEqual( _tablero[0][0], _tablero[1][1], _tablero[2][2], symbol  )
            ||
            areEqual( _tablero[0][2], _tablero[1][1], _tablero[2][0], symbol  )
        ){
            Toast.makeText(applicationContext,
                "winner ,diagonal",
                Toast.LENGTH_SHORT).show()
            return true
        }

        return false
    }

    fun checkJugadaFavorable(posicion : Pair<Int,Int>) : Pair<Int, Int>{
        var x = posicion.first
        var y = posicion.second

        if( _tablero[1][1].isNullOrEmpty() ){
            return Pair(1,1)
        }


        return Pair(x,y)
    }

    fun areEqual(a:String?,b:String?,c:String?, symbol: String): Boolean{

        /*if(symbol in listOf(a,b,c)){
            return true
        }*/
        if( symbol == a && a == b && b == c){
            return  true
        }

        return false
    }

    fun reset_tablero(){
        enable_py = true
        game_ended = false
        turn_number = 0
        for (i in 1..totalCasillas){
            var btn = findViewById<Button>(resources.getIdentifier("button$i","id",packageName))
            btn.setEnabled(true)
            btn.setBackgroundColor(Color.GRAY)
            btn.text = " "

            //tablero = mutableMapOf<Int,String>()
        }
        _tablero = Array(3) { kotlin.arrayOfNulls<kotlin.String>(3) }
        casillasVacias = totalCasillas
    }


    fun isSpace(array: Array<Int>): Boolean{
        for (element in array)
            if (element == null) return true
        return false
    }
}