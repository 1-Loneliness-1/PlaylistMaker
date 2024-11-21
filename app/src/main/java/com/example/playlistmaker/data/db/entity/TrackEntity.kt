package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("tracks")
data class TrackEntity(
    @PrimaryKey @ColumnInfo("track_id")
    val trackId: Long,
    @ColumnInfo("track_title")
    val trackTitle: String,
    @ColumnInfo("artist_name")
    val artistName: String,
    @ColumnInfo("track_duration")
    val trackTimeMillis: String,
    @ColumnInfo("album")
    val album: String?,
    @ColumnInfo("release_date")
    val releaseDate: String,
    @ColumnInfo("genre")
    val genre: String,
    @ColumnInfo("country")
    val country: String,
    @ColumnInfo("song_cover_url")
    val artworkUrl100: String,
    @ColumnInfo("song_preview_url")
    val previewUrl: String
)