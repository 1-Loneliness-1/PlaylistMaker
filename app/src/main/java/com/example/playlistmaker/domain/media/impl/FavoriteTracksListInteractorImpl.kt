package com.example.playlistmaker.domain.media.impl

import com.example.playlistmaker.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.domain.media.FavoriteTracksListInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksListInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksListInteractor {

    override fun getFavoriteTracksForList(): Flow<List<Track>> =
        favoriteTracksRepository.getFavoriteTracks()

}