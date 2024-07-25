package com.example.playlistmaker.domain.search.model

sealed class SearchScreenState {
    object Loading : SearchScreenState()
    data class Content(
        val listOfFoundedTracks: List<Track>
    ) : SearchScreenState()
}