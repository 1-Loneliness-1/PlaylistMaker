package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.media.PlaylistsInteractor
import com.example.playlistmaker.domain.media.model.PlaylistScreenState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val playlistScreenStateLiveData =
        MutableLiveData<PlaylistScreenState>(PlaylistScreenState.EmptyState)

    fun getPlaylistScreenStateLiveData(): LiveData<PlaylistScreenState> {
        return playlistScreenStateLiveData
    }

    fun getAllPlaylistsFromDatabase() {
        viewModelScope.launch {
            playlistsInteractor
                .getAllPlaylists()
                .collect { playlists ->
                    playlistScreenStateLiveData.postValue(
                        if (playlists.isEmpty()) PlaylistScreenState.EmptyState
                        else PlaylistScreenState.ContentState(playlists)
                    )
                }
        }
    }

}