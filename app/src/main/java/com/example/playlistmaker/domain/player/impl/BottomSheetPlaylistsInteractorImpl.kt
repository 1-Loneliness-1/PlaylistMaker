package com.example.playlistmaker.domain.player.impl

import com.example.playlistmaker.domain.db.PlaylistsRepository
import com.example.playlistmaker.domain.db.TracksInPlaylistsRepository
import com.example.playlistmaker.domain.media.model.Playlist
import com.example.playlistmaker.domain.player.BottomSheetPlaylistsInteractor
import com.example.playlistmaker.domain.search.model.Track
import kotlinx.coroutines.flow.Flow

class BottomSheetPlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository,
    private val tracksInPlaylistsRepository: TracksInPlaylistsRepository
) : BottomSheetPlaylistsInteractor {

    override fun addNewPlaylistInDatabase(playlist: Playlist) {
        playlistsRepository.insertNewPlaylist(playlist)
    }

    override fun getAllAvailablePlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getAllPlaylists()
    }

    override fun insertNewTrack(updatedPlaylistId: Long, insertedTrack: Track): Flow<String> {
        return tracksInPlaylistsRepository.insertNewTrack(updatedPlaylistId, insertedTrack)
    }

}