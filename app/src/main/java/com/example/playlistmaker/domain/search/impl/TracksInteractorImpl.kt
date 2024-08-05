package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.model.Track

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consume: (List<Track>) -> Unit) =
        tracksRepository.searchTracks(expression, consume)

}