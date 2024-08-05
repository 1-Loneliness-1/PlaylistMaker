package com.example.playlistmaker.data.mediaplayer

import android.media.MediaPlayer
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerForMusic(
    override val consume: (Int) -> Unit
) : Playable {

    private val mediaPlayer = MediaPlayer()

    companion object {
        private const val STATE_PREPARED = 1
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun stopPlayer() {
        mediaPlayer.stop()
    }

    override fun getCurrentPositionOfPlayer(): String =
        SimpleDateFormat(
            "mm:ss",
            Locale.getDefault()
        ).format(mediaPlayer.currentPosition)

    override fun releaseResourcesForPlayer() = mediaPlayer.release()

    override fun preparePlayer(urlOfMusic: String) {
        mediaPlayer.setDataSource(urlOfMusic)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            consume(STATE_PREPARED)
        }
        mediaPlayer.setOnCompletionListener {
            consume(STATE_PREPARED)
        }
    }
}