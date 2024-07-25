package com.example.playlistmaker.domain.search

import com.example.playlistmaker.domain.search.model.Track

interface TracksInteractor {
    fun searchTracks(expression: String): List<Track>
}