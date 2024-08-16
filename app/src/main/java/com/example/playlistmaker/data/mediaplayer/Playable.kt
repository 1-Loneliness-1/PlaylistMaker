package com.example.playlistmaker.data.mediaplayer

import android.media.MediaPlayer

interface Playable {
    val mediaPlayer: MediaPlayer

    fun startPlayer()

    fun pausePlayer()

    fun stopPlayer()

    fun getCurrentPositionOfPlayer(): String

    fun releaseResourcesForPlayer()

    fun preparePlayer(urlOfMusic: String, consume: (Int) -> Unit)

}