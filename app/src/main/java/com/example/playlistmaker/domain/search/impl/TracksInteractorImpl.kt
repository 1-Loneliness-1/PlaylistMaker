package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {

    override fun searchTracks(expression: String): Flow<List<Track>> =
        tracksRepository.searchTracks(expression)

}