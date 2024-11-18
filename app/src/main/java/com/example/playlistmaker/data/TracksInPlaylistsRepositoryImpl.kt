package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.TrackInPlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.db.TracksInPlaylistsRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TracksInPlaylistsRepositoryImpl(
    private val database: AppDatabase,
    private val convertor: TrackInPlaylistDbConvertor
) : TracksInPlaylistsRepository {

    override fun insertNewTrackInPlaylist(
        updatedPlaylist: Playlist,
        insertedTrack: Track
    ): Flow<String> {
        return flow {
            val tracksInPlaylist =
                database.trackInPlaylistDao().getAllTracksInPlaylist(updatedPlaylist.playlistId)
            val trackForAdd = convertor.map(updatedPlaylist.playlistId, insertedTrack)
            var isTrackAlreadyAdded = false

            if (tracksInPlaylist.isNotEmpty()) {
                for (t in tracksInPlaylist) {
                    if (t.trackId == insertedTrack.trackId) {
                        emit("Трек уже добавлен в плейлист ${updatedPlaylist.playlistTitle}")
                        isTrackAlreadyAdded = true
                        break
                    }
                }

                if (!isTrackAlreadyAdded) {

                    tracksInPlaylist.add(trackForAdd)
                    val listOfTracksId =
                        tracksInPlaylist.map { trackInPlaylist -> trackInPlaylist.trackId }
                    database.trackInPlaylistDao().changeStateOfPlaylist(
                        updatedPlaylist.playlistId,
                        Gson().toJson(listOfTracksId)
                    )
                    database.trackInPlaylistDao().insertNewTrackInPlaylist(trackForAdd)

                    emit("Добавлено в плейлист ${updatedPlaylist.playlistTitle}")
                }
            } else {
                database.trackInPlaylistDao().insertNewTrackInPlaylist(trackForAdd)
                database.trackInPlaylistDao().changeStateOfPlaylist(
                    updatedPlaylist.playlistId,
                    Gson().toJson(listOf(trackForAdd))
                )
                emit("Добавлено в плейлист ${updatedPlaylist.playlistTitle}")
            }
        }
    }

    override fun getAllTracksInPlaylist(selectedPlaylistId: Long): Flow<List<Track>> {
        return flow {
            val tracksInPlaylist =
                database.trackInPlaylistDao().getAllTracksInPlaylist(selectedPlaylistId)
            emit(tracksInPlaylist.map { track -> convertor.map(track) })
        }
    }

    override fun changeStateOfPlaylist(selectedPlaylistId: Long, updatedListOfTracks: String) {
        GlobalScope.launch {
            database.trackInPlaylistDao()
                .changeStateOfPlaylist(selectedPlaylistId, updatedListOfTracks)
        }
    }

}