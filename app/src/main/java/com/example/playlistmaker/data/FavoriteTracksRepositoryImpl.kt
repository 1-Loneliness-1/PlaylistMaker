package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.TrackDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.data.db.entity.FavoriteTrackEntity
import com.example.playlistmaker.domain.db.FavoriteTracksRepository
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FavoriteTracksRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoriteTracksRepository {

    override fun insertTrackToFavorite(trackForInsert: Track) {
        GlobalScope.launch {
            appDatabase.trackDao().insertFavoriteTrack(trackDbConvertor.map(trackForInsert))
        }
    }

    override fun deleteTrackFromFavorites(trackForDelete: Track) {
        GlobalScope.launch {
            appDatabase.trackDao().deleteTrackFromFavorites(trackDbConvertor.map(trackForDelete))
        }
    }

    override fun getFavoriteTracks(): Flow<List<Track>> {
        return flow {
            val favoriteTracks = appDatabase.trackDao().getFavoriteTracks()
            emit(convertFromTrackEntity(favoriteTracks))
        }
    }

    override fun getTrackById(trackId: Long): Flow<Track?> {
        return flow {
            val foundedTrack = appDatabase.trackDao().getTrackById(trackId)
            emit(if (foundedTrack == null) null else trackDbConvertor.map(foundedTrack))
        }
    }

    private fun convertFromTrackEntity(favoriteTracks: List<FavoriteTrackEntity>): List<Track> =
        favoriteTracks.map { track -> trackDbConvertor.map(track) }

}