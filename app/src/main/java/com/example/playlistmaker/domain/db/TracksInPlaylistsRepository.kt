package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksInPlaylistsRepository {

    fun insertNewTrack(updatedPlaylistId: Long, insertedTrack: Track): Flow<String>

    fun deleteTrackFromPlaylist(selectedPlaylistId: Long, deletedTrack: Track)

    fun getTrackInfoById(selectedTrackId: Long): Flow<Track?>

    fun getAllTracks(): Flow<List<Track>>

    fun getAllTracksInPlaylist(selectedPlaylistId: Long): Flow<List<Track>>

}