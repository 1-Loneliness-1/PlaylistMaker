package com.example.playlistmaker.domain.media.model

import com.example.playlistmaker.domain.search.model.Track

sealed class FavoriteTracksScreenState {

    data object EmptyState : FavoriteTracksScreenState()

    data class WithContentState(val favoriteTracks: List<Track>) : FavoriteTracksScreenState()

}