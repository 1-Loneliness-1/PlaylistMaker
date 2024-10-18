package com.example.playlistmaker.domain.player

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksInteractor {

    fun insertTrackToFavorite(trackForAdd: Track)

    fun deleteTrackFromFavorites(trackForDel: Track)

    fun getTrackById(trackId: Long): Flow<Track?>

}