package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo("playlist_id")
    val playlistId: Long = 0,
    @ColumnInfo("title")
    val playlistTitle: String,
    @ColumnInfo("description")
    val playlistDescription: String?,
    @ColumnInfo("cover_path")
    val playlistCoverPath: String?,
    @ColumnInfo("list_of_tracks")
    val listOfTracksInPlaylist: String?,
    @ColumnInfo("track_count", defaultValue = "0")
    val trackCountInPlaylist: Int
)