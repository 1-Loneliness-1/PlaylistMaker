package com.example.playlistmaker.domain.playlist.model

import com.example.playlistmaker.domain.search.model.Track

data class BottomSheetTrackListState(
    val tracksInPlaylist: List<Track>,
    val playlistDuration: String,
    val messageForShare: String
)