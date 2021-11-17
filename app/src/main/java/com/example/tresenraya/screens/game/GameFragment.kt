package com.example.tresenraya.screens.game

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.tresenraya.R
import com.example.tresenraya.databinding.GameFragmentBinding

class GameFragment : Fragment() {

    private lateinit var binding: GameFragmentBinding
    private lateinit var viewModel: GameViewModel

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

        viewModel = ViewModelProvider(this).get(GameViewModel::class.java)

        binding.lifecycleOwner = viewLifecycleOwner
        viewModel.eventGameFinished.observe(viewLifecycleOwner,
            { hasFinished -> if (hasFinished) gameFinished() })

        return inflater.inflate(R.layout.game_fragment, container, false)
    }


    private fun gameFinished(){
        Log.i("GameFragment", "Game has just finished")

        viewModel.onGameFinishComplete()
        val action =
            GameFragmentDirections.actionGameFragmentToScoreFragment()

    }


}