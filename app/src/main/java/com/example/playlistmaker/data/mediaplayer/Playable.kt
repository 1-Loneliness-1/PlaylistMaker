package com.example.playlistmaker.data.mediaplayer

interface Playable {
    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPositionOfPlayer(): String

    fun getCurrentStateOfPlayer(): Int

    fun releaseResourcesForPlayer()

    fun preparePlayer()

}