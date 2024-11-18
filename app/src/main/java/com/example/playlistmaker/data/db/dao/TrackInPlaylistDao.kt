package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackInPlaylistDao {

    @Insert(TrackEntity::class, OnConflictStrategy.IGNORE)
    suspend fun insertNewTrackInPlaylist(insertedTrack: TrackEntity)

    @Query("SELECT * FROM tracks WHERE playlist_id = :selectedPlaylistId")
    suspend fun getAllTracksInPlaylist(selectedPlaylistId: Long): MutableList<TrackEntity>

    @Query("UPDATE playlists SET list_of_tracks = :updatedListOfTracks, track_count = track_count + 1 WHERE playlist_id = :selectedPlaylistId")
    suspend fun changeStateOfPlaylist(selectedPlaylistId: Long, updatedListOfTracks: String)

}