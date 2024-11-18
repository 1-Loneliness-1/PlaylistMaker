package com.example.playlistmaker.domain.playlist.model

import com.example.playlistmaker.domain.media.model.Playlist

sealed class PlaylistInfoScreenState {

    data class ContentState(val playlist: Playlist) : PlaylistInfoScreenState()

}