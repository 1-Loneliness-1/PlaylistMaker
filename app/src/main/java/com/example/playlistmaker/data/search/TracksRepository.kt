package com.example.playlistmaker.data.search

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression: String): Flow<List<Track>>
}