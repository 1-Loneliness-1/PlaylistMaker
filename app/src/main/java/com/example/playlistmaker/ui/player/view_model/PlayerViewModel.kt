package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private val playerStatusLiveData = MutableLiveData<PlayerState>(PlayerState.DefaultState)

    fun getPlayerStatusLiveData(): LiveData<PlayerState> = playerStatusLiveData

    fun startPlayer() {
        playerInteractor.startPlayer()
        playerStatusLiveData.postValue(
            PlayerState.PlayingState(
                if (playerStatusLiveData.value is PlayerState.PreparedState)
                    "00:00"
                else
                    playerInteractor.getCurrentPosition()
            )
        )
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerStatusLiveData.postValue(PlayerState.PausedState)
    }

    fun releasePlayer() =
        playerInteractor.releaseResourcesForPlayer()

    fun preparePlayer(urlOfMusic: String, consume: (Int) -> Unit) {
        playerInteractor.prepPlayer(urlOfMusic, consume)
        playerStatusLiveData.postValue(PlayerState.PreparedState)
    }

    fun updateCurrentPositionOfTrack() {
        playerStatusLiveData.postValue(PlayerState.PlayingState(playerInteractor.getCurrentPosition()))
    }

    fun setPrepState() =
        playerStatusLiveData.postValue(PlayerState.PreparedState)
}