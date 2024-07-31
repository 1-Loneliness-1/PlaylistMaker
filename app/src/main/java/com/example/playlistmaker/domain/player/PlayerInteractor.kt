package com.example.playlistmaker.domain.player

interface PlayerInteractor {
    fun startPlayer()
    fun pausePlayer()
    fun stopPlayer()
    fun getCurrentPosition(): String
    fun releaseResourcesForPlayer()
    fun prepPlayer(urlOfMusic: String)
}