package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.TrackEntity

@Dao
interface TrackInPlaylistDao {

    @Insert(TrackEntity::class, OnConflictStrategy.IGNORE)
    suspend fun insertNewTrack(insertedTrack: TrackEntity)

    @Query("UPDATE playlists SET list_of_tracks = :updatedListOfTracks, track_count = track_count + 1 WHERE playlist_id = :selectedPlaylistId")
    suspend fun insertNewTrackInPlaylistTable(selectedPlaylistId: Long, updatedListOfTracks: String)

    @Delete(TrackEntity::class)
    suspend fun deleteTrack(deletedTrack: TrackEntity)

    @Query("UPDATE playlists SET list_of_tracks = :updatedListOfTracks, track_count = track_count - 1 WHERE playlist_id = :selectedPlaylistId")
    suspend fun deleteTrackByPlaylistTable(selectedPlaylistId: Long, updatedListOfTracks: String?)

    @Query("SELECT * FROM tracks WHERE track_id = :selectedTrackId")
    suspend fun getTrackInfoById(selectedTrackId: Long): TrackEntity?

    @Query("SELECT * FROM tracks")
    suspend fun getAllTracks(): List<TrackEntity>

}