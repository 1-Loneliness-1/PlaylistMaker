package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private var playerStatusLiveData = MutableLiveData<PlayerState>(PlayerState.DefaultState)

    fun getPlayerStatusLiveData(): LiveData<PlayerState> = playerStatusLiveData

    fun startPlayer() {
        playerInteractor.startPlayer()
        playerStatusLiveData.postValue(PlayerState.PlayingState("00:00"))
    }

    fun pausePlayer() {
        playerInteractor.pausePlayer()
        playerStatusLiveData.postValue(PlayerState.PausedState)
    }

    fun releasePlayer() =
        playerInteractor.releaseResourcesForPlayer()

    fun preparePlayer(urlOfMusic: String) {
        playerInteractor.prepPlayer(urlOfMusic)
        playerStatusLiveData.postValue(PlayerState.PreparedState)
    }

    fun updateCurrentPositionOfTrack() {
        playerStatusLiveData.postValue(PlayerState.PlayingState(playerInteractor.getCurrentPosition()))
    }

    fun setPrepState() =
        playerStatusLiveData.postValue(PlayerState.PreparedState)
}