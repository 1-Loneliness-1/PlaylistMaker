package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "tracks_in_playlists",
    primaryKeys = ["foreign_playlist_id", "foreign_track_id"],
    foreignKeys = [
        ForeignKey(
            entity = PlaylistEntity::class,
            childColumns = ["foreign_playlist_id"],
            parentColumns = ["playlist_id"]
        )
    ]
)
data class TracksInPlaylistsEntity(
    @ColumnInfo("foreign_playlist_id")
    val foreignPlaylistId: Long,
    @ColumnInfo("foreign_track_id")
    val foreignTrackId: Long
)