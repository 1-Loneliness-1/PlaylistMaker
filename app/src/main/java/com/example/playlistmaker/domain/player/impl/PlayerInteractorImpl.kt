package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.data.mediaplayer.PlayerRepository
import com.example.playlistmaker.domain.player.PlayerInteractor

class PlayerInteractor(private val mediaPlayerRepository: PlayerRepository) : PlayerInteractor {
    override fun startPlayer() = mediaPlayerRepository.startPlayer()

    override fun pausePlayer() = mediaPlayerRepository.pausePlayer()

    override fun stopPlayer() = mediaPlayerRepository.stopPlayer()

    override fun updatePositionOfTrackTime() =
        mediaPlayerRepository.getCurrentPositionOfTrackTime()

    override fun getCurrentStateOfPlayer() = mediaPlayerRepository.getCurrentStateOfMediaPlayer()

    override fun releaseResourcesForPlayer() = mediaPlayerRepository.releaseResourcesForPlayer()

    override fun prepPlayer() = mediaPlayerRepository.preparePlayer()
}