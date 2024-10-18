package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    fun insertTrackToFavorite(trackForInsert: Track)

    fun deleteTrackFromFavorites(trackForDelete: Track)

    fun getFavoriteTracks(): Flow<List<Track>>

    fun getTrackById(trackId: Long): Flow<Track?>

}