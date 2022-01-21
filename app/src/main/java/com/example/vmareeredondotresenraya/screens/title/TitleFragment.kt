package com.example.vmareeredondotresenraya.screens.title

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.vmareeredondotresenraya.R
import com.example.vmareeredondotresenraya.databinding.TitleFragmentBinding

class TitleFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        // Inflate the layout for this fragment
        val binding: TitleFragmentBinding = DataBindingUtil.inflate(
            inflater, R.layout.title_fragment, container, false)

        binding.playGameButton.setOnClickListener {
            findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToGameFragment() )
        }
        binding.settingsButton.setOnClickListener {
            findNavController().navigate(TitleFragmentDirections.actionTitleFragmentToSettingsFragment() )
        }

        return binding.root
    }


}