package com.example.playlistmaker.domain.media.model

sealed class EditPlaylistScreenState {

    data class ContentState(val currentPlaylist: Playlist) : EditPlaylistScreenState()

}