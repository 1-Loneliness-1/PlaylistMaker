package com.example.playlistmaker.domain.media.model

sealed class PlaylistScreenState {

    data object EmptyState : PlaylistScreenState()

    data class ContentState(val playlists: List<Playlist>) : PlaylistScreenState()

}