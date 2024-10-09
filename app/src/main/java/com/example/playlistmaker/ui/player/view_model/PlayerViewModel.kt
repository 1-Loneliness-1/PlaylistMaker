package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private var updateTimeOfTuneJob: Job? = null

    private val playerStatusLiveData = MutableLiveData<PlayerState>(PlayerState.DefaultState)

    fun getPlayerStatusLiveData(): LiveData<PlayerState> = playerStatusLiveData

    fun startPlayer() {
        playerInteractor.startPlayer()
        updateTimeOfTuneJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                playerStatusLiveData.postValue(PlayerState.PlayingState(playerInteractor.getCurrentPosition()))
                delay(CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY_IN_MILLISEC)
            }
        }
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        updateTimeOfTuneJob?.cancel()
        playerStatusLiveData.postValue(PlayerState.PausedState)
    }

    fun releasePlayer() {
        updateTimeOfTuneJob?.cancel()
        updateTimeOfTuneJob = null
        playerInteractor.releaseResourcesForPlayer()
        playerStatusLiveData.postValue(PlayerState.DefaultState)
    }

    fun preparePlayer(urlOfMusic: String, consume: (Int) -> Unit) {
        playerInteractor.prepPlayer(urlOfMusic, consume)
        playerStatusLiveData.postValue(PlayerState.PreparedState)
    }

    fun setPrepState() {
        updateTimeOfTuneJob?.cancel()
        updateTimeOfTuneJob = null
        playerStatusLiveData.postValue(PlayerState.PreparedState)
    }

    companion object {
        private const val CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY_IN_MILLISEC = 300L
    }

}