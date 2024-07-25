package com.example.playlistmaker.domain.player

interface PlayerInteractor {
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun updatePositionOfTrackTime(): String
    fun getCurrentStateOfPlayer(): Int
    fun releaseResourcesForPlayer()
    fun prepPlayer()
}