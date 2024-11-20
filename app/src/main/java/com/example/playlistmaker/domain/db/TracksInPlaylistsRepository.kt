package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInPlaylistsRepository {

    fun insertNewTrackInPlaylist(updatedPlaylist: Playlist, insertedTrack: Track): Flow<String>

    fun insertNewTrackInPlaylistTable(selectedPlaylistId: Long, updatedListOfTracks: String)

    fun deleteTrackFromPlaylist(updatedPlaylistId: Long, deletedTrack: Track)

    fun deleteTrackFromPlaylistTable(selectedPlaylistId: Long, updatedListOfTracks: String)

    fun deleteAllTracksInPlaylist(deletedPlaylistId: Long)

    fun getAllTracksInPlaylist(selectedPlaylistId: Long): Flow<List<Track>>

}