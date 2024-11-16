package com.example.playlistmaker.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.data.db.entity.PlaylistEntity

@Dao
interface PlaylistDao {

    @Insert(PlaylistEntity::class, OnConflictStrategy.IGNORE)
    suspend fun insertNewPlaylist(playlist: PlaylistEntity)

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Query("INSERT INTO tracks_in_playlists (foreign_playlist_id, foreign_track_id) VALUES (:idOfUpdatedPlaylist, :insertedTrackId)")
    suspend fun addNewTrackInPlaylist(idOfUpdatedPlaylist: Long, insertedTrackId: Long)

    @Query("UPDATE playlists SET list_of_tracks = :listOfIdentifiers, track_count = track_count + 1 WHERE playlist_id = :idOfUpdatedPlaylist")
    suspend fun addNewTrackInListWithTrackId(idOfUpdatedPlaylist: Long, listOfIdentifiers: String)

    @Query("SELECT foreign_track_id FROM tracks_in_playlists WHERE foreign_playlist_id = :selectedPlaylistId")
    suspend fun getAllTracksInPlaylist(selectedPlaylistId: Long): MutableList<Long>

}