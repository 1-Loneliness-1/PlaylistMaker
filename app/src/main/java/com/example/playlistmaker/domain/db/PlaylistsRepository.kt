package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    fun insertNewPlaylist(playlistForInsert: Playlist)

    fun updatePlaylist(
        updatedPlaylistId: Long,
        updatedPlaylistTitle: String,
        updatedPlaylistDescription: String?,
        updatedPlaylistCoverPath: String?
    )

    fun deletePlaylist(deletedPlaylistId: Long)

    fun getPlaylistInfoById(selectedPlaylistId: Long): Flow<Playlist>

    fun getAllPlaylists(): Flow<List<Playlist>>

}