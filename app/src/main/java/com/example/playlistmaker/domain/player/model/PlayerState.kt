package com.example.playlistmaker.domain.player.model

sealed class PlayerState {
    object DefaultState : PlayerState()
    object PreparedState : PlayerState()
    object PlayingState : PlayerState()
    object PausedState : PlayerState()
}