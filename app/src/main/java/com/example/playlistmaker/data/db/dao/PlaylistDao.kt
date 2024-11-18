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

    @Query("SELECT * FROM playlists WHERE playlist_id = :selectedPlaylistId")
    suspend fun getPlaylistInfoById(selectedPlaylistId: Long): PlaylistEntity

    @Query("SELECT * FROM playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

}