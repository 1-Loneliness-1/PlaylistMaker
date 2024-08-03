package com.example.playlistmaker.domain.search.model

sealed class SearchScreenState {
    data class Waiting(
        val tracksInSearchHistory: List<Track>
    ) : SearchScreenState()
    object Loading : SearchScreenState()
    data class Content(
        val listOfFoundedTracks: List<Track>
    ) : SearchScreenState()
}