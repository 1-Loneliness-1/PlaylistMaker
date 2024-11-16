package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class PlaylistsRepositoryImpl(
    private val database: AppDatabase,
    private val convertor: PlaylistDbConvertor
) : PlaylistsRepository {

    override fun insertNewPlaylist(playlistForInsert: Playlist) {
        GlobalScope.launch {
            database.playlistDao().insertNewPlaylist(convertor.map(playlistForInsert))
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return flow {
            val playlists = database.playlistDao().getAllPlaylists()
            emit(playlists.map { playlist -> convertor.map(playlist) })
        }
    }

    override fun addNewTrackInPlaylist(track: Track, playlist: Playlist): Flow<String> {
        return flow {
            val tracksInPlaylist =
                database.playlistDao().getAllTracksInPlaylist(playlist.playlistId)
            var isTrackAlreadyAdded = false

            if (tracksInPlaylist.isNotEmpty()) {
                for (trackId in tracksInPlaylist) {
                    if (trackId == track.trackId) {
                        emit("Трек уже добавлен в плейлист ${playlist.playlistTitle}")
                        isTrackAlreadyAdded = true
                        break
                    }
                }

                if (!isTrackAlreadyAdded) {
                    tracksInPlaylist.add(track.trackId)
                    database.playlistDao().addNewTrackInPlaylist(playlist.playlistId, track.trackId)
                    database
                        .playlistDao()
                        .addNewTrackInListWithTrackId(
                            playlist.playlistId,
                            Gson().toJson(tracksInPlaylist)
                        )
                    emit("Добавлено в плейлист ${playlist.playlistTitle}")
                }
            } else {
                database.playlistDao().addNewTrackInPlaylist(playlist.playlistId, track.trackId)
                database
                    .playlistDao()
                    .addNewTrackInListWithTrackId(
                        playlist.playlistId,
                        Gson().toJson(listOf(track.trackId))
                    )
                emit("Добавлено в плейлист ${playlist.playlistTitle}")
            }
        }
    }

}