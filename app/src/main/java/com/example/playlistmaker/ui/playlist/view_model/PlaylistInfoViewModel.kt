package com.example.playlistmaker.ui.playlist.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.playlist.TracksInPlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.BottomSheetTrackListState
import com.example.playlistmaker.domain.playlist.model.PlaylistInfoScreenState
import com.example.playlistmaker.domain.search.model.Track
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
        }

        updateTrackListInBottomSheet(selectedPlaylistId)
    }

    fun deletePlaylistById(deletedPlaylistId: Long) {
        tracksInPlaylistsInteractor.deletePlaylist(deletedPlaylistId)
    }

    fun deleteTrackFromPlaylist(selectedPlaylistId: Long, deletedTrack: Track) {
        tracksInPlaylistsInteractor.deleteTrackFromPlaylist(selectedPlaylistId, deletedTrack)

        getPlaylistInfoById(selectedPlaylistId)
        updateTrackListInBottomSheet(selectedPlaylistId)
    }

    private fun updateTrackListInBottomSheet(selectedPlaylistId: Long) {
        viewModelScope.launch {
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