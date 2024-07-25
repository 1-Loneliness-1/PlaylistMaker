package com.example.playlistmaker.domain.search.impl

import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.search.TracksInteractor

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String) = tracksRepository.searchTracks(expression)
}