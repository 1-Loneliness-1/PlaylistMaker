package com.example.playlistmaker.data.mediaplayer

interface Playable {
    val consume: (Int) -> Unit

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPositionOfPlayer(): String

    fun releaseResourcesForPlayer()

    fun preparePlayer(urlOfMusic: String)
}