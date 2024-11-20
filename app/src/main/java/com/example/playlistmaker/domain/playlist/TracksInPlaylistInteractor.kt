package com.example.playlistmaker.domain.playlist

import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInPlaylistInteractor {

    fun deleteTrackFromPlaylist(selectedPlaylistId: Long, deletedTrack: Track)

    fun deleteTrackFromPlaylistTable(selectedPlaylistId: Long, updatedListOfTracks: String)

    fun deleteAllTracksInPlaylist(deletedPlaylistId: Long)

    fun getPlaylistInfoById(selectedPlaylistId: Long): Flow<Playlist>

    fun getAllTracksInPlaylist(selectedPlaylistId: Long): Flow<List<Track>>

}