package com.example.playlistmaker.data

import com.example.playlistmaker.data.converters.PlaylistDbConvertor
import com.example.playlistmaker.data.db.AppDatabase
import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.media.model.Playlist
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
            val playlistForDelete = database.playlistDao().getPlaylistInfoById(deletedPlaylistId)
            database.playlistDao().deletePlaylist(playlistForDelete)
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