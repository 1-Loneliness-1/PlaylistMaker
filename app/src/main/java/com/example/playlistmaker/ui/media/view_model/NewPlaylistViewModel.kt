package com.example.playlistmaker.ui.media.view_model

import androidx.lifecycle.ViewModel
import com.example.playlistmaker.domain.media.PlaylistsInteractor
import com.example.playlistmaker.domain.media.model.Playlist

class NewPlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor
) : ViewModel() {

    fun savePlaylistInDatabase(playlist: Playlist) {
        playlistsInteractor.insertNewPlaylist(playlist)
    }

}