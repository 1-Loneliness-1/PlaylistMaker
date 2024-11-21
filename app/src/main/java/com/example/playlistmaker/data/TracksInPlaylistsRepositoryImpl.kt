package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.TrackInPlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.db.TracksInPlaylistsRepository
import com.example.playlistmaker.domain.search.model.Track
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class TracksInPlaylistsRepositoryImpl(
    private val database: AppDatabase,
    private val convertor: TrackInPlaylistDbConvertor
) : TracksInPlaylistsRepository {

    override fun insertNewTrack(
        updatedPlaylistId: Long,
        insertedTrack: Track
    ): Flow<String> {
        return flow {
            val currentPlaylist = database.playlistDao().getPlaylistInfoById(updatedPlaylistId)

            if (currentPlaylist.listOfTracksInPlaylist != null) {
                val itemType = object : TypeToken<MutableList<Long>>() {}.type
                val tracksInCurrentPlaylist = Gson().fromJson<MutableList<Long>>(
                    currentPlaylist.listOfTracksInPlaylist,
                    itemType
                )

                if (tracksInCurrentPlaylist.contains(insertedTrack.trackId)) {
                    emit("Трек уже добавлен в плейлист ${currentPlaylist.playlistTitle}")
                } else {
                    database.trackInPlaylistDao().insertNewTrack(convertor.map(insertedTrack))
                    tracksInCurrentPlaylist.add(insertedTrack.trackId)
                    database.trackInPlaylistDao().insertNewTrackInPlaylistTable(
                        updatedPlaylistId,
                        Gson().toJson(tracksInCurrentPlaylist)
                    )

                    emit("Добавлено в плейлист ${currentPlaylist.playlistTitle}")
                }
            } else {
                database.trackInPlaylistDao().insertNewTrack(convertor.map(insertedTrack))
                database.trackInPlaylistDao().insertNewTrackInPlaylistTable(
                    updatedPlaylistId,
                    Gson().toJson(listOf(insertedTrack.trackId))
                )
                emit("Добавлено в плейлист ${currentPlaylist.playlistTitle}")
            }
        }
    }

    override fun deleteTrackFromPlaylist(selectedPlaylistId: Long, deletedTrack: Track) {
        GlobalScope.launch {
            val currentPlaylist = database.playlistDao().getPlaylistInfoById(selectedPlaylistId)

            if (currentPlaylist.listOfTracksInPlaylist != null) {
                val itemType = object : TypeToken<MutableList<Long>>() {}.type
                val tracksInCurrentPlaylist = Gson().fromJson<MutableList<Long>>(
                    currentPlaylist.listOfTracksInPlaylist,
                    itemType
                )
                tracksInCurrentPlaylist.remove(deletedTrack.trackId)
                if (tracksInCurrentPlaylist.size == 0) {
                    database.trackInPlaylistDao()
                        .deleteTrackByPlaylistTable(selectedPlaylistId, null)
                } else {
                    database.trackInPlaylistDao().deleteTrackByPlaylistTable(
                        selectedPlaylistId,
                        Gson().toJson(tracksInCurrentPlaylist)
                    )
                }
            }

            val allPlaylists = database.playlistDao().getAllPlaylists()
            var isTrackUsed = false
            for (playlist in allPlaylists) {
                if (playlist.listOfTracksInPlaylist != null) {
                    val typeOfList = object : TypeToken<MutableList<Long>>() {}.type
                    val trackIdsInPlaylist = Gson().fromJson<MutableList<Long>>(
                        playlist.listOfTracksInPlaylist,
                        typeOfList
                    )

                    if (trackIdsInPlaylist.contains(deletedTrack.trackId)) {
                        isTrackUsed = true
                        break
                    }
                }
            }

            if (!isTrackUsed) {
                database.trackInPlaylistDao().deleteTrack(convertor.map(deletedTrack))
            }

        }
    }

    override fun getTrackInfoById(selectedTrackId: Long): Flow<Track?> {
        return flow {
            val foundedTrack = database.trackInPlaylistDao().getTrackInfoById(selectedTrackId)
            if (foundedTrack != null) {
                emit(convertor.map(foundedTrack))
            } else {
                emit(null)
            }
        }
    }

    override fun getAllTracks(): Flow<List<Track>> {
        return flow {
            val currentTracksInTable = database.trackInPlaylistDao().getAllTracks()
                .map { trackForDb -> convertor.map(trackForDb) }
            emit(currentTracksInTable)
        }
    }

    override fun getAllTracksInPlaylist(selectedPlaylistId: Long): Flow<List<Track>> {
        return flow {
            val listOfTracksInPlaylist = mutableListOf<Track>()
            val currentPlaylist = database.playlistDao().getPlaylistInfoById(selectedPlaylistId)
            val typeOfList = object : TypeToken<List<Long>>() {}.type
            val trackIdsInPlaylist =
                Gson().fromJson<List<Long>>(currentPlaylist.listOfTracksInPlaylist, typeOfList)

            if (currentPlaylist.listOfTracksInPlaylist != null) {
                trackIdsInPlaylist.forEach { trackId ->
                    val trackForAdd = database.trackInPlaylistDao().getTrackInfoById(trackId)
                    if (trackForAdd != null) {
                        listOfTracksInPlaylist.add(convertor.map(trackForAdd))
                    }
                }

            }

            emit(listOfTracksInPlaylist.toList())
        }
    }

}