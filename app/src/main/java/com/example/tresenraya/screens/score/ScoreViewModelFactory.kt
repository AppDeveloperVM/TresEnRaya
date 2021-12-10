package com.example.tresenraya.screens.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ScoreViewModelFactory(private val finalScore: Int, private val winner: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
        return ScoreViewModel(finalScore,winner) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
        }
}