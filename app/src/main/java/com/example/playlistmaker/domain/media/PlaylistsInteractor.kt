package com.example.playlistmaker.domain.media

import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    fun insertNewPlaylist(playlist: Playlist)

    fun getPlaylistInfoById(selectedPlaylistId: Long): Flow<Playlist>

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun updatePlaylist(
        updatedPlaylistId: Long,
        updatedPlaylistTitle: String,
        updatedPlaylistDescription: String?,
        updatedPlaylistCoverPath: String?
    )

}