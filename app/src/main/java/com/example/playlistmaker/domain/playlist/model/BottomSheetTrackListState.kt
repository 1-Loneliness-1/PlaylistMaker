package com.example.playlistmaker.domain.playlist.model

import com.example.playlistmaker.domain.search.model.Track

sealed class BottomSheetTrackListState {

    data class ContentState(val tracksInPlaylist: List<Track>) : BottomSheetTrackListState()

}