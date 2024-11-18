package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.TracksInPlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.BottomSheetTrackListState
import com.example.playlistmaker.domain.playlist.model.PlaylistInfoScreenState
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val tracksInPlaylistsInteractor: TracksInPlaylistInteractor
) : ViewModel() {

    private val playlistInfoScreenStatusLiveData = MutableLiveData<PlaylistInfoScreenState>()
    private val bottomSheetTrackListStatusLiveData = MutableLiveData<BottomSheetTrackListState>()

    fun getPlaylistInfoScreenStatusLiveData(): LiveData<PlaylistInfoScreenState> {
        return playlistInfoScreenStatusLiveData
    }

    fun getBottomSheetTrackListStatusLiveData(): LiveData<BottomSheetTrackListState> {
        return bottomSheetTrackListStatusLiveData
    }

    fun getPlaylistInfoById(selectedPlaylistId: Long) {
        viewModelScope.launch {
            tracksInPlaylistsInteractor.getPlaylistInfoById(selectedPlaylistId)
                .collect { playlist ->
                    playlistInfoScreenStatusLiveData.postValue(
                        PlaylistInfoScreenState.ContentState(
                            playlist
                        )
                    )
                }
            tracksInPlaylistsInteractor.getAllTracksInPlaylist(selectedPlaylistId)
                .collect { listOfTracks ->
                    bottomSheetTrackListStatusLiveData.postValue(
                        BottomSheetTrackListState.ContentState(
                            listOfTracks
                        )
                    )
                }
        }
    }

}