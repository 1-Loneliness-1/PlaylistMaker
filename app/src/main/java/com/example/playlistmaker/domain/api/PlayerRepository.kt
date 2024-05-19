package com.example.playlistmaker.domain.api

interface PlayerRepository {
    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPositionOfTrackTime(): String

    fun getCurrentStateOfMediaPlayer(): Int

    fun releaseResourcesForPlayer()

    fun preparePlayer()
}