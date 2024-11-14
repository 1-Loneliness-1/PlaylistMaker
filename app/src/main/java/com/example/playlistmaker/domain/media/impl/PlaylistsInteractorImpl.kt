package com.example.playlistmaker.domain.media.impl

import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.media.PlaylistsInteractor
import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
) : PlaylistsInteractor {

    override fun insertNewPlaylist(playlist: Playlist) {
        playlistsRepository.insertNewPlaylist(playlist)
    }

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getAllPlaylists()
    }
}