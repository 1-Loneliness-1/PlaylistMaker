package com.example.playlistmaker.presentation.player

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
import com.example.playlistmaker.R
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.domain.entities.Track
import com.example.playlistmaker.domain.usecases.PlayerInteractor
import com.example.playlistmaker.utils.DimenConvertor
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private lateinit var playerInteractor: PlayerInteractor
    private lateinit var playStopButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var updateTextViewRunnable: Runnable

    companion object {
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private const val CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY = 500L
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

        playerInteractor = Creator.providePlayerInteractor(currentTrack.previewUrl)
        playerInteractor.prepPlayer()

        updateTextViewRunnable = object : Runnable {
            override fun run() {
                when (playerInteractor.getCurrentStateOfPlayer()) {
                    STATE_PLAYING -> {
                        currentTrackTime.text = playerInteractor.updatePositionOfTrackTime()
                        mainHandler.postDelayed(this, CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY)
                    }

                    STATE_PREPARED -> {
                        mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
                        currentTrackTime.text = getString(R.string.start_time)
                        playStopButton.setImageResource(R.drawable.play_icon)
                    }
                }
            }
        }

        playStopButton.setOnClickListener {
            when (playerInteractor.getCurrentStateOfPlayer()) {
                STATE_PREPARED, STATE_PAUSED -> {
                    playStopButton.setImageResource(R.drawable.pause_icon)
                    playerInteractor.startPlayer()
                    mainHandler.post(updateTextViewRunnable)
                }

                STATE_PLAYING -> {
                    playStopButton.setImageResource(R.drawable.play_icon)
                    playerInteractor.pausePlayer()
                    mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        playerInteractor.pausePlayer()
        mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
        playStopButton.setImageResource(R.drawable.play_icon)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
        playerInteractor.releaseResourcesForPlayer()
    }
}