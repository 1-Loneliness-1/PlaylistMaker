package com.example.playlistmaker.data.mediaplayer

interface PlayerRepository {
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun getCurrentPositionOfTrackTime(): String
    fun releaseResourcesForPlayer()
    fun preparePlayer(urlOfMusic: String, consume: (Int) -> Unit)
    fun isPlaying(): Boolean
}