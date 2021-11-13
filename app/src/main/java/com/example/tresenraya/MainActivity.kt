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
    private var actual_player: String = "";
    private lateinit var symbol_player: String

    private lateinit var reset: Button

    private var mc_turn_end : Boolean = false
    private var enable_py: Boolean = true
    private val machine_turn_time : Long = 100

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

        //'X' per player, 'O' per machine
        symbol_player = if(turnNumber == 0) "X" else "O"
    }

    //--------------TURNOS-----------------
    fun turno_inicial() {
        turnNumber = 0;
        enable_py = true
        player_turn()
    }

    fun player_turn(){
        Log.d("Waiting", " for player turn to end.")
        mc_turn_end = false
        enable_py = true
    }

    fun machine_turn(){
        enable_py = false

        Log.d("Waiting", " for machine turn to end.")

        Handler(Looper.getMainLooper()).postDelayed({
            var machine_pos: Pair<Int,Int> = Pair(0,0)

            do{
                machine_pos =  checkJugadaFavorable()
                val empty = is_position_empty( machine_pos )
            }while ( !empty && machine_pos == null ) //mientras que la jugada sugerida no sea válida

            var pos = get_casilla_id(machine_pos)
            buttonSelected(pos)
            mc_turn_end = true

            if(!game_ended) {
                check_turno()
                if(casillasVacias > 0){
                    cambio_turno()
                }else{
                    end_game()
                }
            }

        }, machine_turn_time) // miliseconds
    }

    fun check_turno(){
        actual_player = players[turnNumber] //
        Log.d("Turno", "$actual_player")
        symbol_player = if(turnNumber == 0) "X" else "O"

        turntxt.text = actual_player
    }

    fun cambio_turno() {
        if(!game_ended) {
            if (turnNumber == 1) {
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
    }

    fun handleButtonClick(view: View) {
        if(enable_py && !game_ended){
            hacer_jugada(view as Button)


            if(!game_ended) {
                check_turno()
                if(casillasVacias > 0){
                    cambio_turno()
                }else{
                    end_game()
                }
            }
        }
    }

    fun hacer_jugada(btn: Button){
        var btn_id = btn.getTag().toString()
        var id:Int = Integer.parseInt(btn_id)
        Log.d("Id", "$btn_id")

        //id de celda
        var posicion_x_y = get_tablero_position(id);
        var x = posicion_x_y.first
        var y = posicion_x_y.second


        check_turno()

        _tablero[x][y] = symbol_player
        Log.d("added", "Id:"+id+" -> pos: "+x+","+y )

        if ( checkWinnerPlay(get_tablero_position(id), symbol_player) ){
            end_game()
        }

        //btn.setEnabled(false)
        if(turnNumber == 0){
            btn.setBackgroundColor(Color.GREEN)
        }else if(turnNumber == 1){
            btn.setBackgroundColor(Color.RED)
        }
        btn.setEnabled(false)

        Log.d( players[turnNumber], " turn ended.")

        turnNumber = (turnNumber + 1) % 2
        casillasVacias--
    }

    fun is_position_empty(position : Pair<Int,Int>): Boolean{

        val x = position.first
        val y = position.second

        val pos_value = _tablero[x][y]
        Log.d("Is pos empty? pos", "$pos_value , coords: $x, $y")

        if( pos_value.isNullOrBlank() ){
            return true
        }

        return false
    }

    fun get_coords_value(position : Pair<Int,Int>) : String? {
        val x = position.first
        val y = position.second

        var pos_value = _tablero[x][y]

        if(! pos_value.isNullOrBlank() ){
            return pos_value
        }

        return null
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


    fun get_tablero_position(casilla: Int) : Pair<Int, Int>{
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
        //var nuevo_array = Pair()
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

    fun checkWinnerPlay(posicion : Pair<Int,Int>, symbol : String): Boolean{
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
            winner = players[turnNumber]
            return true
        }

        return false
    }

    fun checkJugadaFavorable() : Pair<Int, Int>{

        // H ( 0,1,2,  3,4,5,  6,7,8 ) +1
        // V ( 0,3,6,  1,4,7,  2,5,8 ) +3
        // D ( 0,4,8,  2,4,6 ) +2
        var symbols = arrayOf("O","X")

        var combinaciones = arrayOf(
            intArrayOf(0, 1, 2),
            intArrayOf(3, 4, 5),
            intArrayOf(6, 7, 8),
            intArrayOf(0, 3, 6),
            intArrayOf(1, 4, 7),
            intArrayOf(2, 5, 8),
            intArrayOf(0, 4, 8),
            intArrayOf(2, 4, 6)
        )

        // combinations for attack and defend
        for(player in symbols){

            for(coords in combinaciones){
                // 3 positions from combination
                    //return posicion favorable

                //primero y segundo
                if( get_coords_value(get_tablero_position(coords[0])) == get_coords_value(get_tablero_position(coords[1])) &&
                    get_coords_value(get_tablero_position(coords[0])) == player /*symbol_player*/
                    && get_coords_value(get_tablero_position(coords[2])) == null
                ){
                    //return tercera posicion
                    return get_tablero_position(coords[2])

                //primero y tercero
                }else if(
                    get_coords_value(get_tablero_position(coords[0])) == get_coords_value(get_tablero_position(coords[2])) &&
                    get_coords_value(get_tablero_position(coords[0])) == player
                    && get_coords_value(get_tablero_position(coords[1])) == null
                ){
                    //return segunda posicion
                    return get_tablero_position(coords[1])

                //segundo y tercero
                }else if(get_coords_value(get_tablero_position(coords[1])) == get_coords_value(get_tablero_position(coords[2])) &&
                    get_coords_value(get_tablero_position(coords[1])) == player
                    && get_coords_value(get_tablero_position(coords[0])) == null
                ){
                    //return primera posicion
                    return get_tablero_position(coords[0])

                }

            }

        }

        var coords : Pair<Int,Int> = Pair(0,0)
        do {
            var pos = Random().nextInt(9) // random number
            coords = get_tablero_position(pos)
            val empty = is_position_empty( coords )
        }while (! empty && coords!= null)

        return coords
    }


    fun areEqual(a:String?,b:String?,c:String?, symbol: String): Boolean{

        if( symbol == a && a == b && b == c){
            return  true
        }
        return false
    }

    fun reset_tablero(){
        enable_py = true
        game_ended = false
        turnNumber = 0
        for (i in 1..totalCasillas){
            var btn = findViewById<Button>(resources.getIdentifier("button$i","id",packageName))
            btn.setEnabled(true)
            btn.setBackgroundColor(Color.GRAY)
            btn.text = " "

            //tablero = mutableMapOf<Int,String>()
        }
        _tablero = Array(3) { kotlin.arrayOfNulls<kotlin.String>(3) }
        casillasVacias = totalCasillas

        check_turno()
    }

}