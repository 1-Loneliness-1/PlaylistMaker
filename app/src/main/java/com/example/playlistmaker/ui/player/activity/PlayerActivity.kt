package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.player.model.PlayerState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.utils.DimenConvertor
import com.google.gson.Gson

class PlayerActivity : AppCompatActivity() {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val consume: (Int) -> Unit = { _ ->
        mainHandler.removeCallbacksAndMessages(null)
        viewModel.setPrepState()
    }
    private val updateTextViewRunnable = object : Runnable {
        override fun run() {
            viewModel.updateCurrentPositionOfTrack()
            mainHandler.postDelayed(this, CURRENT_COUNT_OF_SECONDS_UPDATE_DELAY)
        }
    }
    private val viewModel by viewModels<PlayerViewModel> {
        PlayerViewModel.getViewModelFactory(consume)
    }

    private lateinit var playStopButton: ImageButton
    private lateinit var currentTrackTime: TextView
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var currentTrack: Track

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
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backToPrevScreenButton = binding.ivBackToPrevScr
        val songCover = binding.ivSongCover
        val songTitle = binding.tvSongTitle
        val artistName = binding.tvAuthorOfSong
        val trackDuration = binding.tvTrackTimeChanging
        val album = binding.tvAlbumNameChanging
        val groupOfAlbumInfo = binding.gAlbumInfo
        val yearOfSoundPublished = binding.tvYearOfSongChanging
        val genreOfSong = binding.tvGenreChanging
        val countryOfSong = binding.tvCountryOfSongChanging

        //Initialize variables of class
        playStopButton = binding.ibPlayStop
        currentTrackTime = binding.tvCurrentTrackTime

        viewModel.getPlayerStatusLiveData().observe(this) { playerState ->
            when (playerState) {
                is PlayerState.DefaultState -> {
                    changeStateOfElements(STATE_DEFAULT)
                }

                is PlayerState.PreparedState -> {
                    changeStateOfElements(STATE_PREPARED)
                }

                is PlayerState.PlayingState -> {
                    changeStateOfElements(STATE_PLAYING)
                    currentTrackTime.text = playerState.currentPosition
                }

                is PlayerState.PausedState -> {
                    mainHandler.removeCallbacksAndMessages(null)
                    changeStateOfElements(STATE_PAUSED)
                }
            }
        }

        backToPrevScreenButton.setOnClickListener { finish() }

        val json: String? = intent.getStringExtra(KEY_FOR_INTENT_DATA)
        currentTrack = Gson().fromJson(json, Track::class.java)
        if (json != null) {
            Glide.with(this)
                .load(currentTrack.artworkUrl100.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.song_cover_placeholder)
                .centerCrop()
                .transform(RoundedCorners(DimenConvertor.dpToPx(8f, this)))
                .into(songCover)
            songTitle.text = currentTrack.trackName
            artistName.text = currentTrack.artistName
            trackDuration.text = currentTrack.trackTimeMillis
            groupOfAlbumInfo.isVisible = currentTrack.collectionName != null
            album.text = currentTrack.collectionName
            yearOfSoundPublished.text = currentTrack.releaseDate.split("-")[0]
            genreOfSong.text = currentTrack.primaryGenreName
            countryOfSong.text = currentTrack.country
        } else {
            Toast.makeText(this, "Произошла ошибка!", Toast.LENGTH_SHORT).show()
        }

        viewModel.preparePlayer(currentTrack.previewUrl)

        playStopButton.setOnClickListener {
            when (viewModel.getPlayerStatusLiveData().value) {
                is PlayerState.PreparedState, PlayerState.PausedState -> {
                    viewModel.startPlayer()
                }

                is PlayerState.PlayingState -> {
                    viewModel.pausePlayer()
                }

                else -> {
                    //Nothing to do))
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        viewModel.pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
        viewModel.releasePlayer()
    }

    // Fun for changing state of elements on the screen by current state of player
    private fun changeStateOfElements(currentState: Int) {
        when (currentState) {
            STATE_DEFAULT -> {
                binding.ibPlayStop.isEnabled = false
            }

            STATE_PREPARED -> {
                playStopButton.isEnabled = true
                playStopButton.setImageResource(R.drawable.play_icon)
                mainHandler.removeCallbacksAndMessages(null)
                currentTrackTime.text = resources.getString(R.string.start_time)
            }

            STATE_PLAYING -> {
                binding.ibPlayStop.setImageResource(R.drawable.pause_icon)
                mainHandler.post(updateTextViewRunnable)
            }

            STATE_PAUSED -> {
                binding.ibPlayStop.setImageResource(R.drawable.play_icon)
                mainHandler.removeCallbacksAndMessages(updateTextViewRunnable)
            }
        }
    }

}