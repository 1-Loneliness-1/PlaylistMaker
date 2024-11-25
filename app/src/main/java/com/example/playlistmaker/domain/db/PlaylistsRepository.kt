package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    suspend fun insertNewPlaylist(playlistForInsert: Playlist)

    suspend fun updatePlaylist(
        updatedPlaylistId: Long,
        updatedPlaylistTitle: String,
        updatedPlaylistDescription: String?,
        updatedPlaylistCoverPath: String?
    )

    suspend fun deletePlaylist(deletedPlaylistId: Long)

    fun getPlaylistInfoById(selectedPlaylistId: Long): Flow<Playlist>

    fun getAllPlaylists(): Flow<List<Playlist>>

}