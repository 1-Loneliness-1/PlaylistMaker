package com.example.playlistmaker.domain.media.model

data class Playlist(
    val playlistId: Long,
    val playlistTitle: String,
    val playlistDescription: String?,
    val playlistCoverPath: String?,
    val listOfTracksInPlaylist: String?,
    val trackCountInPlaylist: Int
)