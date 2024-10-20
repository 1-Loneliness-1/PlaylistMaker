package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.domain.player.FavoriteTracksInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractorImpl(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : FavoriteTracksInteractor {

    override fun insertTrackToFavorite(trackForAdd: Track) {
        favoriteTracksRepository.insertTrackToFavorite(trackForAdd)
    }

    override fun deleteTrackFromFavorites(trackForDel: Track) {
        favoriteTracksRepository.deleteTrackFromFavorites(trackForDel)
    }

    override fun getTrackById(trackId: Long): Flow<Track?> {
        return favoriteTracksRepository.getTrackById(trackId)
    }

}