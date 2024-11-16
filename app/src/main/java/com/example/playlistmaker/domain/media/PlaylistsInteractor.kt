package com.example.playlistmaker.domain.media

import com.example.playlistmaker.domain.media.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {

    fun insertNewPlaylist(playlist: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

}