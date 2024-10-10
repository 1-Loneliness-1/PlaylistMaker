package com.example.playlistmaker.data.mediaplayer.impl

import com.example.playlistmaker.data.mediaplayer.Playable
import com.example.playlistmaker.data.mediaplayer.PlayerRepository

class PlayerRepositoryImpl(
    private val mediaPlayer: Playable
) : PlayerRepository {

    override fun startPlayer() = mediaPlayer.startPlayer()

    override fun pausePlayer() = mediaPlayer.pausePlayer()

    override fun stopPlayer() = mediaPlayer.stopPlayer()

    override fun getCurrentPositionOfTrackTime() =
        mediaPlayer.getCurrentPositionOfPlayer()

    override fun releaseResourcesForPlayer() =
        mediaPlayer.releaseResourcesForPlayer()

    override fun preparePlayer(urlOfMusic: String, consume: (Int) -> Unit) =
        mediaPlayer.preparePlayer(urlOfMusic, consume)

    override fun isPlaying(): Boolean =
        mediaPlayer.isPlaying()

}