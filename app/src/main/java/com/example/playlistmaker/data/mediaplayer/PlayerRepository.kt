package com.example.playlistmaker.data.mediaplayer

interface PlayerRepository {
    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPositionOfTrackTime(): String

    fun getCurrentStateOfMediaPlayer(): Int

    fun releaseResourcesForPlayer()

    fun preparePlayer()
}