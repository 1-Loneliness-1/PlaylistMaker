package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.api.PlayerRepository

class PlayerInteractor(private val mediaPlayerRepository: PlayerRepository) {
    fun startPlayer() = mediaPlayerRepository.startPlayer()

    fun pausePlayer() = mediaPlayerRepository.pausePlayer()

    fun stopPlayer() = mediaPlayerRepository.stopPlayer()

    fun updatePositionOfTrackTime() =
        mediaPlayerRepository.getCurrentPositionOfTrackTime()

    fun getCurrentStateOfPlayer() = mediaPlayerRepository.getCurrentStateOfMediaPlayer()

    fun releaseResourcesForPlayer() = mediaPlayerRepository.releaseResourcesForPlayer()

    fun prepPlayer() = mediaPlayerRepository.preparePlayer()
}