package com.example.playlistmaker.domain.media.impl

import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.media.PlaylistsInteractor
import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
) : PlaylistsInteractor {

    override suspend fun insertNewPlaylist(playlist: Playlist) {
        playlistsRepository.insertNewPlaylist(playlist)
    }

    override fun getPlaylistInfoById(selectedPlaylistId: Long): Flow<Playlist> {
        return playlistsRepository.getPlaylistInfoById(selectedPlaylistId)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getAllPlaylists()
    }

    override suspend fun updatePlaylist(
        updatedPlaylistId: Long,
        updatedPlaylistTitle: String,
        updatedPlaylistDescription: String?,
        updatedPlaylistCoverPath: String?
    ) {
        playlistsRepository.updatePlaylist(
            updatedPlaylistId,
            updatedPlaylistTitle,
            updatedPlaylistDescription,
            updatedPlaylistCoverPath
        )
    }
}