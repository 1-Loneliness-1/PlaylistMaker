package com.example.playlistmaker.domain.player.model

sealed class PlayerScreenState {
    object Loading : PlayerScreenState()
    data class Content(
        val trackModel: TrackModel
    ) : PlayerScreenState()
}