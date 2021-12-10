package com.example.tresenraya.screens.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.tresenraya.R
import com.example.tresenraya.databinding.ScoreFragmentBinding

class ScoreFragment : Fragment() {

    private lateinit var viewModel: ScoreViewModel
    private lateinit var viewModelFactory: ScoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate view and obtain an instance of the binding class.
        val binding: ScoreFragmentBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.score_fragment,
            container,
            false
        )

        val score: Int = ScoreFragmentArgs.fromBundle(requireArguments()).score
        val winner: Int = ScoreFragmentArgs.fromBundle(requireArguments()).winner
        viewModelFactory = ScoreViewModelFactory(score,winner)
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ScoreViewModel::class.java)

        binding.scoreViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

//        viewModel.score.observe(viewLifecycleOwner) { newScore ->
//            binding.scoreText.text = newScore.toString()
//        }

        viewModel.eventPlayAgain.observe(viewLifecycleOwner) { playAgain ->
            if (playAgain) {
                //val bundle = bundleOf("score" to 22)
                findNavController().navigate(ScoreFragmentDirections.actionScoreFragmentToGameFragment() )

                viewModel.onPlayAgainComplete()
            }
        }


        return binding.root
    }

}