package com.example.playlistmaker.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_tracks")
data class TrackEntity(
    @PrimaryKey @ColumnInfo("track_id")
    val trackId: String,
    @ColumnInfo("track_name")
    val trackName: String,
    @ColumnInfo("artist_name")
    val artistName: String,
    @ColumnInfo("track_time")
    val trackTimeMillis: String,
    @ColumnInfo("collection_title")
    val collectionName: String?,
    @ColumnInfo("release_date")
    val releaseDate: String,
    @ColumnInfo("genre")
    val primaryGenreName: String,
    @ColumnInfo("country")
    val country: String,
    @ColumnInfo("album_cover_url")
    val artworkUrl100: String,
    @ColumnInfo("song_preview_url")
    val previewUrl: String
)