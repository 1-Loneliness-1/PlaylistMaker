package com.example.playlistmaker.domain.media

import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksListInteractor {

    fun getFavoriteTracksForList(): Flow<List<Track>>

}