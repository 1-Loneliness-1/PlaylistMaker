package com.example.playlistmaker.ui.playlist.view_model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.playlist.TracksInPlaylistInteractor
import com.example.playlistmaker.domain.playlist.model.BottomSheetTrackListState
import com.example.playlistmaker.domain.playlist.model.PlaylistInfoScreenState
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistInfoViewModel(
    private val tracksInPlaylistsInteractor: TracksInPlaylistInteractor,
    private val application: Application
) : AndroidViewModel(application) {

    private var messageForShare = ""
    private var isPlaylistEmpty = false

    private val playlistInfoScreenStatusLiveData = MutableLiveData<PlaylistInfoScreenState>()
    private val bottomSheetTrackListStatusLiveData = MutableLiveData<BottomSheetTrackListState>()

    fun getPlaylistInfoScreenStatusLiveData(): LiveData<PlaylistInfoScreenState> {
        return playlistInfoScreenStatusLiveData
    }

    fun getBottomSheetTrackListStatusLiveData(): LiveData<BottomSheetTrackListState> {
        return bottomSheetTrackListStatusLiveData
    }

    fun getPlaylistInfoById(selectedPlaylistId: Long) {
        viewModelScope.launch {
            tracksInPlaylistsInteractor.getPlaylistInfoById(selectedPlaylistId)
                .collect { playlist ->
                    messageForShare =
                        "${playlist.playlistTitle}${if (playlist.playlistDescription == null) "" else "\n" + playlist.playlistDescription}\n" +
                                application.resources.getQuantityString(
                                    R.plurals.track_plurals,
                                    playlist.trackCountInPlaylist,
                                    playlist.trackCountInPlaylist
                                )

                    playlistInfoScreenStatusLiveData.postValue(PlaylistInfoScreenState(playlist))
                }

            updateTrackListInBottomSheet(selectedPlaylistId)
        }

    }

    fun deletePlaylistById(deletedPlaylistId: Long) {
        viewModelScope.launch {
            tracksInPlaylistsInteractor.deletePlaylist(deletedPlaylistId)
        }
    }

    fun deleteTrackFromPlaylist(selectedPlaylistId: Long, deletedTrack: Track) {
        viewModelScope.launch {
            tracksInPlaylistsInteractor.deleteTrackFromPlaylist(selectedPlaylistId, deletedTrack)
            getPlaylistInfoById(selectedPlaylistId)
        }
    }

    private fun updateTrackListInBottomSheet(selectedPlaylistId: Long) {
        viewModelScope.launch {
            tracksInPlaylistsInteractor.getAllTracksInPlaylist(selectedPlaylistId)
                .collect { listOfTracks ->
                    val oldDateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
                    val targetDateFormat = SimpleDateFormat("mm", Locale.getDefault())
                    var playlistDuration = 0

                    isPlaylistEmpty = listOfTracks.isEmpty()

                    if (!isPlaylistEmpty) {
                        var index = 1

                        for (track in listOfTracks) {
                            val trackDurationInMinAndSec =
                                oldDateFormat.parse(track.trackTimeMillis)
                            val trackDurationInMin =
                                targetDateFormat.format(trackDurationInMinAndSec!!)
                            playlistDuration += trackDurationInMin.toInt()

                            messageForShare =
                                messageForShare.plus("\n${index}. ${track.artistName} - ${track.trackName} (${track.trackTimeMillis})")
                            index++
                        }
                    }

                    val playlistDurationText =
                        application.resources.getQuantityString(
                            R.plurals.minute_plurals,
                            playlistDuration,
                            playlistDuration
                        )
                    bottomSheetTrackListStatusLiveData.postValue(
                        BottomSheetTrackListState(
                            listOfTracks,
                            playlistDurationText,
                            messageForShare
                        )
                    )
                }
        }
    }

}