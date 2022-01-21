package com.example.vmareeredondotresenraya.screens.game

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.vmareeredondotresenraya.R
import com.example.vmareeredondotresenraya.databinding.GameFragmentBinding

class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding
    private lateinit var viewModel: GameViewModel
    private lateinit var buttons: Array<View>
    //var tablero = Array<Int>(9) { 0 }
    var tablero = Array(3) { arrayOfNulls<String>(3) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.game_fragment,
            container,
            false
        )

        buttons = arrayOf(
            binding.button1,
            binding.button2,
            binding.button3,
            binding.button4,
            binding.button5,
            binding.button6,
            binding.button7,
            binding.button8,
            binding.button9
        )

        viewModel = ViewModelProvider(this)[GameViewModel::class.java]

        binding.gameViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        tablero = viewModel._tablero ?: Array(3) { arrayOfNulls<String>(3) }


        viewModel.turnoIA.observe(viewLifecycleOwner,
            Observer{
                IA(it);
            }
        );

        viewModel.restartTablero.observe(viewLifecycleOwner,
            Observer{
               restartTablero()
            }
        );

        viewModel.eventGameFinished.observe(viewLifecycleOwner,
            { hasFinished -> if (hasFinished) gameFinished() })

        return binding.root
    }


    fun IA(position: Int) {
        buttons[position].setBackgroundColor(Color.parseColor("#f44336"))
    }

    fun restartTablero(){
        for (button in buttons){
            button.setBackgroundColor(Color.parseColor("#606060"))
        }
    }

    private fun gameFinished(){
        Log.i("GameFragment", "Game has just finished")

        viewModel.onGameFinishComplete()
        val action = GameFragmentDirections.actionGameFragmentToScoreFragment()
        action.score = viewModel.score.value ?: 0
        action.winner = viewModel.winner.value ?: 0
        NavHostFragment.findNavController(this).navigate(action)
    }




}