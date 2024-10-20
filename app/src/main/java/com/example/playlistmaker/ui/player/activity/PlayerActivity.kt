package com.example.playlistmaker.ui.player.activity

import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.domain.player.model.FavoriteTrackButtonState
import com.example.playlistmaker.domain.player.model.PlayerState
import com.example.playlistmaker.domain.search.model.Track
import com.example.playlistmaker.ui.player.view_model.PlayerViewModel
import com.example.playlistmaker.utils.DimenConvertor
import com.google.gson.Gson
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {

    private val consume: (Int) -> Unit = { _ ->
        viewModel.setPrepState()
    }
    private val viewModel: PlayerViewModel by viewModel()

    private var playStopButton: ImageButton? = null
    private var currentTrackTime: TextView? = null
    private var binding: ActivityPlayerBinding? = null
    private var currentTrack: Track? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val backToPrevScreenButton = binding?.ivBackToPrevScr
        val songCover = binding?.ivSongCover
        val songTitle = binding?.tvSongTitle
        val artistName = binding?.tvAuthorOfSong
        val trackDuration = binding?.tvTrackTimeChanging
        val album = binding?.tvAlbumNameChanging
        val groupOfAlbumInfo = binding?.gAlbumInfo
        val yearOfSoundPublished = binding?.tvYearOfSongChanging
        val genreOfSong = binding?.tvGenreChanging
        val countryOfSong = binding?.tvCountryOfSongChanging
        val favoriteTrackButton = binding?.ibLike

        playStopButton = binding?.ibPlayStop
        currentTrackTime = binding?.tvCurrentTrackTime

        viewModel.getPlayerStatusLiveData().observe(this) { playerState ->
            changeStateOfElements(playerState)
            if (playerState is PlayerState.PlayingState) {
                currentTrackTime?.text = playerState.currentPosition
            }
        }

        viewModel.getFavorTrackButtonStatusLiveData().observe(this) { favorTrackButtonState ->
            when (favorTrackButtonState) {
                is FavoriteTrackButtonState.IsNotFavoriteState -> {
                    favoriteTrackButton?.setImageResource(R.drawable.like_icon)
                }

                is FavoriteTrackButtonState.FavoriteState -> {
                    favoriteTrackButton?.setImageResource(R.drawable.liked_icon)
                }
            }
            favoriteTrackButton?.isEnabled = true
            favoriteTrackButton?.isClickable = true
            favoriteTrackButton?.alpha = 1.0f
        }

        favoriteTrackButton?.setOnClickListener {
            favoriteTrackButton.isEnabled = false
            favoriteTrackButton.isClickable = false
            favoriteTrackButton.alpha = 0.5f
            if (viewModel.getFavorTrackButtonStatusLiveData().value is FavoriteTrackButtonState.IsNotFavoriteState) {
                viewModel.insertFavoriteTrackInDb(currentTrack!!)
            } else {
                viewModel.deleteTrackFromFavorite(currentTrack!!)
            }
        }

        backToPrevScreenButton?.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val json: String? = intent.getStringExtra(KEY_FOR_INTENT_DATA)
        currentTrack = Gson().fromJson(json, Track::class.java)
        if (json != null) {
            Glide.with(this)
                .load(currentTrack?.artworkUrl100?.replace("100x100bb.jpg", "512x512bb.jpg"))
                .placeholder(R.drawable.song_cover_placeholder)
                .centerCrop()
                .transform(
                    RoundedCorners(
                        DimenConvertor.dpToPx(
                            NUMBER_OF_DP_FOR_IMAGE_CORNERS_ROUNDING,
                            this
                        )
                    )
                )
                .into(songCover!!)
            songTitle?.text = currentTrack?.trackName
            artistName?.text = currentTrack?.artistName
            trackDuration?.text = currentTrack?.trackTimeMillis
            groupOfAlbumInfo?.isVisible = currentTrack?.collectionName != null
            album?.text = currentTrack?.collectionName
            yearOfSoundPublished?.text =
                currentTrack?.releaseDate?.split("-")?.get(POSITION_NUMBER_OF_YEAR_PUBLICATION)
            genreOfSong?.text = currentTrack?.primaryGenreName
            countryOfSong?.text = currentTrack?.country

            lifecycleScope.launch {
                viewModel.getStatusOfFavorTrackButton(currentTrack?.trackId!!)
            }

        } else {
            Toast.makeText(this, "Произошла ошибка!", Toast.LENGTH_SHORT).show()
        }

        viewModel.preparePlayer(currentTrack?.previewUrl!!, consume)

        playStopButton?.setOnClickListener {
            when (viewModel.getPlayerStatusLiveData().value) {
                is PlayerState.PreparedState, PlayerState.PausedState -> {
                    viewModel.startPlayer()
                }
                is PlayerState.PlayingState -> {
                    viewModel.pausePlayer()
                }
                else -> {

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
        viewModel.releasePlayer()
    }

    private fun changeStateOfElements(currentState: PlayerState) {
        when (currentState) {
            is PlayerState.DefaultState -> {
                playStopButton?.isClickable = false
                playStopButton?.alpha = 0.5f
            }

            is PlayerState.PreparedState -> {
                playStopButton?.isClickable = true
                playStopButton?.alpha = 1.0f
                playStopButton?.setImageResource(R.drawable.play_icon)
                currentTrackTime?.text = resources.getString(R.string.start_time)
            }

            is PlayerState.PlayingState -> {
                playStopButton?.setImageResource(R.drawable.pause_icon)
            }

            is PlayerState.PausedState -> {
                playStopButton?.setImageResource(R.drawable.play_icon)
            }
        }
    }

    companion object {
        private const val KEY_FOR_INTENT_DATA = "Selected track"
        private const val NUMBER_OF_DP_FOR_IMAGE_CORNERS_ROUNDING = 8f
        private const val POSITION_NUMBER_OF_YEAR_PUBLICATION = 0
    }

}