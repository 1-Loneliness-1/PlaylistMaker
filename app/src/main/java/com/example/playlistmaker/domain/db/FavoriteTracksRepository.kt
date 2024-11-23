package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {

    suspend fun insertTrackToFavorite(trackForInsert: Track)

    suspend fun deleteTrackFromFavorites(trackForDelete: Track)

    fun getFavoriteTracks(): Flow<List<Track>>

    fun getTrackById(trackId: Long): Flow<Track?>

}