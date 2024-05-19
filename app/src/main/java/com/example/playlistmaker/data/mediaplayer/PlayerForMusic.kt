package com.example.playlistmaker.data.mediaplayer

import android.media.MediaPlayer
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerForMusic(private val urlOfMusic: String) : Playable {

    private val mediaPlayer = MediaPlayer()
    private var playerState: Int = STATE_DEFAULT

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    override fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
    }

    override fun stopPlayer() {
        playerState = STATE_PREPARED
    }

    override fun getCurrentPositionOfPlayer(): String =
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)

    override fun getCurrentStateOfPlayer() = playerState

    override fun releaseResourcesForPlayer() = mediaPlayer.release()

    override fun preparePlayer() {
        mediaPlayer.setDataSource(urlOfMusic)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
        }
    }
}