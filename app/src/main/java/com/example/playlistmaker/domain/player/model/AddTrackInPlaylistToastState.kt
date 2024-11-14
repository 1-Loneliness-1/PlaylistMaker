package com.example.playlistmaker.domain.player.model

sealed class AddTrackInPlaylistToastState {
    data class Content(val messageForToast: String) : AddTrackInPlaylistToastState()
}