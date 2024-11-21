package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    override fun updatePlaylist(
        updatedPlaylistId: Long,
        updatedPlaylistTitle: String,
        updatedPlaylistDescription: String?,
        updatedPlaylistCoverPath: String?
    ) {
        GlobalScope.launch {
            database.playlistDao().updatePlaylist(
                updatedPlaylistId,
                updatedPlaylistTitle,
                updatedPlaylistDescription,
                updatedPlaylistCoverPath
            )
        }
    }

    override fun deletePlaylist(deletedPlaylistId: Long) {
        GlobalScope.launch {
            var isTrackUsed = false
            val playlistForDelete = database.playlistDao().getPlaylistInfoById(deletedPlaylistId)
            val typeOfList = object : TypeToken<MutableList<Long>>() {}.type
            val trackIdsInDeletedPlaylist = Gson().fromJson<MutableList<Long>>(
                playlistForDelete.listOfTracksInPlaylist,
                typeOfList
            )
            database.playlistDao().deletePlaylist(playlistForDelete)

            if (playlistForDelete.listOfTracksInPlaylist != null) {

            }

            val allPlaylists = database.playlistDao().getAllPlaylists()
            for (trackId in trackIdsInDeletedPlaylist) {
                for (playlist in allPlaylists) {
                    val trackIdInCurrentList = Gson().fromJson<MutableList<Long>>(
                        playlist.listOfTracksInPlaylist,
                        typeOfList
                    )
                    if (trackIdInCurrentList.contains(trackId)) {
                        isTrackUsed = true
                    }
                    if (isTrackUsed) {
                        break
                    }
                }

                if (!isTrackUsed) {
                    val trackForDelete =
                        database.trackInPlaylistDao().getTrackInfoById(deletedPlaylistId)
                    if (trackForDelete != null) {
                        database.trackInPlaylistDao().deleteTrack(trackForDelete)
                    }
                }

                isTrackUsed = false
            }

        }
    }

    override fun getPlaylistInfoById(selectedPlaylistId: Long): Flow<Playlist> {
        return flow {
            emit(convertor.map(database.playlistDao().getPlaylistInfoById(selectedPlaylistId)))
        }
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return flow {
            val playlists = database.playlistDao().getAllPlaylists()
            emit(playlists.map { playlist -> convertor.map(playlist) })
        }
    }

}