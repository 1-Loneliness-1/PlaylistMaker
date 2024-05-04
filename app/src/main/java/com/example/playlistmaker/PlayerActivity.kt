package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.utils.DimenConvertor
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private val mediaPlayer = MediaPlayer()
    private val mainHandler = Handler(Looper.getMainLooper())
    private var playerState = STATE_DEFAULT
    private lateinit var playStopButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var updateTextViewRunnable: Runnable

    companion object {
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private const val CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY = 500L
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        val backToPrevScreenButton = findViewById<ImageView>(R.id.ivBackToPrevScr)
        val songCover = findViewById<ImageView>(R.id.ivSongCover)
        val songTitle = findViewById<TextView>(R.id.tvSongTitle)
        val artistName = findViewById<TextView>(R.id.tvAuthorOfSong)
        val trackDuration = findViewById<TextView>(R.id.tvTrackTimeChanging)
        val album = findViewById<TextView>(R.id.tvAlbumNameChanging)
        val groupOfAlbumInfo = findViewById<androidx.constraintlayout.widget.Group>(R.id.gAlbumInfo)
        val yearOfSoundPublished = findViewById<TextView>(R.id.tvYearOfSongChanging)
        val genreOfSong = findViewById<TextView>(R.id.tvGenreChanging)
        val countryOfSong = findViewById<TextView>(R.id.tvCountryOfSongChanging)

        //Initialize variables of class
        playStopButton = findViewById(R.id.ibPlayStop)
        currentTrackTime = findViewById(R.id.tvCurrentTrackTime)

        playStopButton.isEnabled = false

        backToPrevScreenButton.setOnClickListener { finish() }

        val json: String? = intent.getStringExtra(KEY_FOR_INTENT_DATA)
        val currentTrack: Track = Gson().fromJson(json, Track::class.java)
        if (json != null) {
            Glide.with(this)
                .load(currentTrack.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.song_cover_placeholder)
                .centerCrop()
                .transform(RoundedCorners(DimenConvertor.dpToPx(8f, this)))
                .into(songCover)
            songTitle.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackDuration.text = SimpleDateFormat(
                "mm:ss",
                Locale.getDefault()
            ).format(
                currentTrack.trackTimeMillis
            )
            groupOfAlbumInfo.isVisible = currentTrack.collectionName != null
            album.text = currentTrack.collectionName
            yearOfSoundPublished.text = currentTrack.releaseDate.split("-")[0]
            genreOfSong.text = currentTrack.primaryGenreName
            countryOfSong.text = currentTrack.country
        } else {
            Toast.makeText(this, "Произошла ошибка!", Toast.LENGTH_SHORT).show()
        }

        updateTextViewRunnable = object : Runnable {
            override fun run() {
                if (playerState == STATE_PLAYING)
                    currentTrackTime.text = SimpleDateFormat(
                        "mm:ss",
                        Locale.getDefault()
                    ).format(mediaPlayer.currentPosition)
                mainHandler.postDelayed(this, CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY)
            }
        }

        preparePlayer(currentTrack.previewUrl)

        playStopButton.setOnClickListener {
            when (playerState) {
                STATE_PREPARED, STATE_PAUSED -> {
                    playStopButton.setImageResource(R.drawable.pause_icon)
                    mediaPlayer.start()
                    playerState = STATE_PLAYING
                    mainHandler.post(updateTextViewRunnable)
                }

                STATE_PLAYING -> {
                    playStopButton.setImageResource(R.drawable.play_icon)
                    mediaPlayer.pause()
                    playerState = STATE_PAUSED
                    mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        mediaPlayer.pause()
        mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
        playerState = STATE_PAUSED
        playStopButton.setImageResource(R.drawable.play_icon)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
        mediaPlayer.release()
    }

    private fun preparePlayer(songExampleUrl: String) {
        mediaPlayer.setDataSource(songExampleUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playStopButton.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
            currentTrackTime.text = getString(R.string.start_time)
            playStopButton.setImageResource(R.drawable.play_icon)
        }
    }
}