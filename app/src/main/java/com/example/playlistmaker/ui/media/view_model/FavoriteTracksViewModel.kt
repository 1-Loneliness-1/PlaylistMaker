package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.media.FavoriteTracksListInteractor
import com.example.playlistmaker.domain.media.model.FavoriteTracksScreenState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(
    private val favoriteTracksListInteractor: FavoriteTracksListInteractor
) : ViewModel() {

    private val favoriteTracksScreenStateLiveData =
        MutableLiveData<FavoriteTracksScreenState>(FavoriteTracksScreenState.EmptyState)

    fun getFavoriteTracksScreenStateLiveData(): LiveData<FavoriteTracksScreenState> =
        favoriteTracksScreenStateLiveData

    fun getFavoriteTracksFromBd() {
        viewModelScope.launch {
            favoriteTracksListInteractor
                .getFavoriteTracksForList()
                .collect { favoriteTracks ->
                    favoriteTracksScreenStateLiveData.postValue(
                        if (favoriteTracks.isEmpty()) FavoriteTracksScreenState.EmptyState
                        else FavoriteTracksScreenState.WithContentState(favoriteTracks)
                    )
                }
        }
    }

}