package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.data.mediaplayer.PlayerRepository
import com.example.playlistmaker.domain.player.PlayerInteractor

class PlayerInteractorImpl(
    private val mediaPlayerRepository: PlayerRepository
) : PlayerInteractor {

    override fun startPlayer() = mediaPlayerRepository.startPlayer()

    override fun pausePlayer() = mediaPlayerRepository.pausePlayer()

    override fun stopPlayer() = mediaPlayerRepository.stopPlayer()

    override fun getCurrentPosition() =
        mediaPlayerRepository.getCurrentPositionOfTrackTime()

    override fun releaseResourcesForPlayer() = mediaPlayerRepository.releaseResourcesForPlayer()

    override fun prepPlayer(urlOfMusic: String, consume: (Int) -> Unit) =
        mediaPlayerRepository.preparePlayer(urlOfMusic, consume)

    override fun isPlaying(): Boolean =
        mediaPlayerRepository.isPlaying()
}