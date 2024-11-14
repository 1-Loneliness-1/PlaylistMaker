package com.example.playlistmaker.domain.db

import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {

    fun insertNewPlaylist(playlistForInsert: Playlist)

    fun getAllPlaylists(): Flow<List<Playlist>>

    fun addNewTrackInPlaylist(track: Track, playlist: Playlist): Flow<String>

}