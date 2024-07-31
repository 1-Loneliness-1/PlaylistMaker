package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.player.impl.PlayerInteractor
import com.example.playlistmaker.domain.player.model.PlayerState

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

    private var playerStatusLiveData = MutableLiveData<PlayerState>(PlayerState.DefaultState)

    fun getPlayerStatusLiveData(): LiveData<PlayerState> = playerStatusLiveData

    companion object {
        fun getViewModelFactory(consume: (Int) -> Unit): ViewModelProvider.Factory =
            viewModelFactory {
                initializer {
                    PlayerViewModel(
                        Creator.providePlayerInteractor(consume)
                    )
                }
            }
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        playerStatusLiveData.postValue(PlayerState.PlayingState)
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

    fun getCurrentPositionOfTrack(): String =
        playerInteractor.getCurrentPosition()

    fun setPrepState() =
        playerStatusLiveData.postValue(PlayerState.PreparedState)
}