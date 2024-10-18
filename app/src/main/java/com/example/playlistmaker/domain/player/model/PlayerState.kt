package com.example.playlistmaker.domain.player.model

sealed class PlayerState {

    data object DefaultState : PlayerState()

    data object PreparedState : PlayerState()

    data class PlayingState(val currentPosition: String) : PlayerState()

    data object PausedState : PlayerState()
}