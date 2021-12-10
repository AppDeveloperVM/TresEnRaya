package com.example.tresenraya.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(finalScore: Int) : ViewModel() {
    // The final score
    private val _score = MutableLiveData<Int>(finalScore)
    val score: LiveData<Int>
        get() = _score

    private val _finalScore = MutableLiveData<String>()
    val finalScore: LiveData<String>
        get() = _finalScore

    private val _winner = MutableLiveData<Int>(40)
    val winner: LiveData<Int>
        get() = _winner

    private val _winnerText = MutableLiveData<String>()
    val winnerText: LiveData<String>
        get() = _winnerText

    private val _eventPlayAgain = MutableLiveData<Boolean>(false)
    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    init {
        if(_winner.value==0){
            _winnerText.value = "el jugador"
            _finalScore.value = "Has perdido.."
        }else{
            _winnerText.value = "máquina"
            _finalScore.value = "Ganaste!"
        }

        Log.i("ScoreViewModel", "Final score is ${_score.value}")
        Log.i("Winner", " is ${_winnerText.value}")
        _winnerText.value = "Winner is ${_winnerText.value}"
    }

    fun onPlayAgain() {
        _eventPlayAgain.value = true
    }

    fun onPlayAgainComplete() {
        _eventPlayAgain.value = false
    }

}