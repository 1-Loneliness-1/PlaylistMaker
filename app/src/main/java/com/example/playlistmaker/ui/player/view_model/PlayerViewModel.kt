package com.example.playlistmaker.ui.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.player.FavoriteTracksInteractor
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.player.model.FavoriteTrackButtonState
import com.example.playlistmaker.domain.player.model.PlayerState
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {

    private var updateTimeOfTuneJob: Job? = null

    private val playerStatusLiveData = MutableLiveData<PlayerState>(PlayerState.DefaultState)
    private val favorTrackButtonStatusLiveData =
        MutableLiveData<FavoriteTrackButtonState>(FavoriteTrackButtonState.IsNotFavoriteState)

    fun getPlayerStatusLiveData(): LiveData<PlayerState> = playerStatusLiveData

    fun getFavorTrackButtonStatusLiveData(): LiveData<FavoriteTrackButtonState> =
        favorTrackButtonStatusLiveData

    override fun onCleared() {
        super.onCleared()
        updateTimeOfTuneJob?.cancel()
    }

    fun startPlayer() {
        playerInteractor.startPlayer()
        updateTimeOfTuneJob?.cancel()
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
        playerInteractor.releaseResourcesForPlayer()
        playerStatusLiveData.postValue(PlayerState.DefaultState)
    }

    fun preparePlayer(urlOfMusic: String, consume: (Int) -> Unit) {
        playerInteractor.prepPlayer(urlOfMusic, consume)
        playerStatusLiveData.postValue(PlayerState.PreparedState)
    }

    fun setPrepState() {
        updateTimeOfTuneJob?.cancel()
        playerStatusLiveData.postValue(PlayerState.PreparedState)
    }

    suspend fun getStatusOfFavorTrackButton(currentTrackId: Long) {
        favoriteTracksInteractor
            .getTrackById(currentTrackId)
            .collect { track ->
                favorTrackButtonStatusLiveData.postValue(
                    if (track == null) FavoriteTrackButtonState.IsNotFavoriteState
                    else FavoriteTrackButtonState.FavoriteState
                )
            }

    }

    fun insertFavoriteTrackInDb(trackForInsert: Track) {
        favoriteTracksInteractor.insertTrackToFavorite(trackForInsert)
        favorTrackButtonStatusLiveData.postValue(FavoriteTrackButtonState.FavoriteState)
    }

    fun deleteTrackFromFavorite(trackForDelete: Track) {
        favoriteTracksInteractor.deleteTrackFromFavorites(trackForDelete)
        favorTrackButtonStatusLiveData.postValue(FavoriteTrackButtonState.IsNotFavoriteState)
    }

    companion object {
        private const val CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY_IN_MILLISEC = 300L
    }

}