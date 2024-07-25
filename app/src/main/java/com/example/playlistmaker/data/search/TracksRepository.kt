package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.search.model.Track

interface TracksRepository {
    fun searchTracks(expression: String): List<Track>
}