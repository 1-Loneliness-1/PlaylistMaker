package com.example.playlistmaker.domain.player.model

sealed class PlayerState {
    object DefaultState : PlayerState()
    object PreparedState : PlayerState()
    data class PlayingState(
        val currentPosition: String
    ) : PlayerState()
    object PausedState : PlayerState()
}