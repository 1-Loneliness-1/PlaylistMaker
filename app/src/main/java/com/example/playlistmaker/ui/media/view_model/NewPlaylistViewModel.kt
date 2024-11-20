package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.domain.media.PlaylistsInteractor
import com.example.playlistmaker.domain.media.model.EditPlaylistScreenState
import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.launch

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    private val editPlaylistScreenStatusLiveData = MutableLiveData<EditPlaylistScreenState>()

    fun getEditPlaylistScreenStatusLiveData(): LiveData<EditPlaylistScreenState> {
        return editPlaylistScreenStatusLiveData
    }

    fun savePlaylistInDatabase(playlist: Playlist) {
        playlistsInteractor.insertNewPlaylist(playlist)
    }

    fun getInfoAboutPlaylistById(selectedPlaylistId: Long) {
        viewModelScope.launch {
            playlistsInteractor
                .getPlaylistInfoById(selectedPlaylistId)
                .collect { playlist ->
                    editPlaylistScreenStatusLiveData.postValue(
                        EditPlaylistScreenState.ContentState(playlist)
                    )
                }
        }
    }

    fun updatePlaylist(
        updatedPlaylistId: Long,
        updatedPlaylistTitle: String,
        updatedPlaylistDescription: String?,
        updatedPlaylistCoverPath: String?
    ) {
        playlistsInteractor.updatePlaylist(
            updatedPlaylistId,
            updatedPlaylistTitle,
            updatedPlaylistDescription,
            updatedPlaylistCoverPath
        )
    }

}